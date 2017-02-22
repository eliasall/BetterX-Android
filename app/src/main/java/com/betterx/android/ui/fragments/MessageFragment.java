package com.betterx.android.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.betterx.android.R;
import com.betterx.android.app.events.MessageReceivedEvent;
import com.betterx.android.classes.MainThreadBus;
import com.betterx.android.data.PersistentDataStore;
import com.betterx.android.dataModel.Message;
import com.betterx.android.dataModel.MessageType;
import com.betterx.android.network.ApiClient;
import com.betterx.android.ui.adapters.MessageAdapter;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class MessageFragment extends BaseFragment {

    @Inject
    PersistentDataStore dataStore;
    @Inject
    ApiClient apiClient;
    @Inject
    MainThreadBus bus;

    @Bind(R.id.messages_list)
    ListView listView;
    @Bind(R.id.messages_et_send)
    EditText messageField;

    private MessageAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDaggerComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_messages, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dataStore.markAllMessagesAsReaded();
        final List<Message> messageList = dataStore.getMessages();
        Collections.reverse(messageList);
        adapter = new MessageAdapter(getActivity(), messageList);//dataStore.getMessages());
        listView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    @Subscribe
    public void onNewMessageReceived(MessageReceivedEvent event) {
        final List<Message> messageList = new ArrayList<>(dataStore.getMessages());
        Collections.reverse(messageList);
        adapter.clear();
        adapter.addAll(messageList);
        listView.setSelectionAfterHeaderView();
    }

    @OnClick(R.id.messages_btn_send)
    public void onSendMessage() {
        final Message message = new Message();
        message.msg = messageField.getText().toString();
        message.date = System.currentTimeMillis();
        message.messageType = MessageType.SENT;
        message.isReaded = true;
        dataStore.saveMessage(message);

        final Map<String, String> request = new HashMap<>();
        request.put("data1", dataStore.getGcmRegId());
        request.put("data3", message.msg);
        apiClient.sendMessage(request, new Callback<Object>() {
            @Override
            public void success(Object o, Response response) {
                Timber.d("Messages successfully sended! " + o);
            }

            @Override
            public void failure(RetrofitError error) {
                Timber.e(error, error.getMessage());
            }
        });
        onNewMessageReceived(new MessageReceivedEvent());
        messageField.setText("");
    }

}
