package com.betterx.android.dataModel;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.RawContacts;

public class ContactToShare {

    public long id;
    public Uri photoUri;
    public String name;
    public String email;

    @Override
    public String toString() {
        return "ContactToShare{" +
                "id=" + id +
                ", photoUri=" + photoUri +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    protected Uri getContactPhotoUri(long contactId) {
        return ContentUris.withAppendedId(Contacts.CONTENT_URI, contactId);
    }


    public static ContactToShare parseContact(Cursor cursor) {
        final ContactToShare contact = new ContactToShare();
        final long photoContactId = cursor.getLong(cursor.getColumnIndex(CommonDataKinds.Photo.CONTACT_ID));
        contact.id = cursor.getLong(cursor.getColumnIndex(RawContacts._ID));
        contact.name = cursor.getString(cursor.getColumnIndex(Contacts.DISPLAY_NAME));
        contact.email = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Email.DATA));
        contact.photoUri = contact.getContactPhotoUri(photoContactId);
        return contact;
    }

}
