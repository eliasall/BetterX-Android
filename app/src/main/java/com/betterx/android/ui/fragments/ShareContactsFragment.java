package com.betterx.android.ui.fragments;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.betterx.android.R;
import com.betterx.android.data.PersistentDataStore;
import com.betterx.android.dataModel.ContactToShare;
import com.betterx.android.dataModel.Tickets;
import com.betterx.android.services.SaveStatsService;
import com.betterx.android.ui.adapters.ShareWithContactAdapter;
import com.betterx.featureslogger.data.UIDGenerator;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;

public class ShareContactsFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    public static final int ID_SHARE = 1;

    @Inject
    PersistentDataStore dataStore;

    @Bind(R.id.share_contacts_list)
    ListView listView;

    private ShareWithContactAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDaggerComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_share_contacts, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final List<ContactToShare> contacts = getAvailableForSharingContacts();
        adapter = new ShareWithContactAdapter(getActivity(), contacts);
        listView.setOnItemClickListener(this);
        listView.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.add(0, ID_SHARE, 0, R.string.share).setIcon(getShareBtnDrawable())
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == ID_SHARE) {
            sendInvites(getEmailsToShare());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0) {
            saveTicketsCount();
            showToast("You share the app " + dataStore.getSharesCount() + " times");
            goBack();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final ContactToShare contact = adapter.getItem(position);
        adapter.changeSelection(contact);
    }

    private List<ContactToShare> getAvailableForSharingContacts() {
        final List<ContactToShare> result = new ArrayList<>();
        final Cursor cursor = getContactsWithEmailCursor();
        if(cursor != null) {
            if (cursor.moveToFirst()){
                do {
                    final ContactToShare contact = ContactToShare.parseContact(cursor);
                    result.add(contact);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return result;
    }

    private Cursor getContactsWithEmailCursor() {
        final ContentResolver cr = getActivity().getContentResolver();
        final String[] PROJECTION = new String[] { ContactsContract.RawContacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.PHOTO_ID,
                ContactsContract.CommonDataKinds.Email.DATA,
                ContactsContract.CommonDataKinds.Photo.CONTACT_ID };
        final String order = "CASE WHEN " + ContactsContract.Contacts.DISPLAY_NAME
                + " NOT LIKE '%@%' THEN 1 ELSE 2 END, " + ContactsContract.Contacts.DISPLAY_NAME
                + ", " + ContactsContract.CommonDataKinds.Email.DATA + " COLLATE NOCASE";
        String filter = ContactsContract.CommonDataKinds.Email.DATA + " NOT LIKE ''";
        return cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, PROJECTION, filter, null, order);
    }

    private void sendInvites(String[] emails) {
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, emails);
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.mail_subject));
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.share_msg));
        emailIntent.setType("text/plain");
        startActivityForResult(Intent.createChooser(emailIntent, getString(R.string.choose_client)), 0);
    }

    private String[] getEmailsToShare() {
        final List<ContactToShare> contacts = adapter.getSelectedContacts();
        final String[] result = new String[contacts.size()];
        for(int i = 0; i < contacts.size(); i++) {
            result[i] = contacts.get(i).email;
        }
        return result;
    }

    private Drawable getShareBtnDrawable() {
        Drawable drawable = getResources().getDrawable(R.drawable.abc_ic_menu_share_mtrl_alpha);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable.mutate(), Color.WHITE);
        return drawable;
    }

    private void saveTicketsCount() {
        dataStore.saveShareCount(adapter.getSelectedContacts().size());

        final Tickets tickets = new Tickets();
        tickets.uid = UIDGenerator.getUID(getActivity());
        tickets.tickets = dataStore.getSharesCount();
        tickets.timestamp = System.currentTimeMillis();

        final Gson gson = new Gson();
        SaveStatsService.saveStats(getActivity(), gson.toJson(tickets), getTicketsFileName());
    }

    private String getTicketsFileName() {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
        final String uid = UIDGenerator.getUID(getActivity());
        return String.format("%s_tickets_%s", uid, dateFormat.format(new Date(System.currentTimeMillis())));
    }


}
