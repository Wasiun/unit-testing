package com.techyourchance.testdrivendevelopment.exercise8;

import com.techyourchance.testdrivendevelopment.exercise8.contacts.Contact;
import com.techyourchance.testdrivendevelopment.exercise8.networking.ContactSchema;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint.Callback;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint.FailReason;

import java.util.ArrayList;
import java.util.List;

import static com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint.FailReason.GENERAL_ERROR;
import static com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint.FailReason.NETWORK_ERROR;

public class FetchContactsUseCase {

    public interface Listener {
        void onContactsFetched(List<Contact> capture);
        void onContactsFailure(FailReason capture);
    }

    private final List<Listener> listeners = new ArrayList<>();
    private final GetContactsHttpEndpoint getContactsHttpEndpoint;

    public FetchContactsUseCase(GetContactsHttpEndpoint getContactsHttpEndpoint) {
        this.getContactsHttpEndpoint = getContactsHttpEndpoint;
    }

    public void fetchContactsAndNotify(String filter) {
        getContactsHttpEndpoint.getContacts(filter, new Callback() {
            @Override
            public void onGetContactsSucceeded(List<ContactSchema> contactItems) {
                for(Listener listener: listeners){
                    listener.onContactsFetched(contactsFromSchemas(contactItems));
                }
            }

            @Override
            public void onGetContactsFailed(FailReason failReason) {
                if(failReason == GENERAL_ERROR) {
                    for (Listener listener : listeners) {
                        listener.onContactsFailure(GENERAL_ERROR);
                    }
                }else if(failReason == NETWORK_ERROR){
                    for (Listener listener : listeners) {
                        listener.onContactsFailure(NETWORK_ERROR);
                    }
                }
            }
        });
    }

    private List<Contact> contactsFromSchemas(List<ContactSchema> contactItems) {
        List<Contact> contactList = new ArrayList<>();
        for(ContactSchema schema: contactItems) {
            contactList.add(new Contact(schema.getId(), schema.getFullName(),schema.getImageUrl() ));
        }
        return contactList                   ;
    }

    public void registerListeners(Listener listener) {
        listeners.add(listener);
    }

    public void unregisterListers(Listener listener) {
        listeners.remove(listener);
    }

}
