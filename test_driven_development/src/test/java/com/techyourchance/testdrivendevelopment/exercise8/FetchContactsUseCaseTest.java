package com.techyourchance.testdrivendevelopment.exercise8;

import com.techyourchance.testdrivendevelopment.exercise8.contacts.Contact;
import com.techyourchance.testdrivendevelopment.exercise8.networking.ContactSchema;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint.Callback;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint.FailReason;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(MockitoJUnitRunner.class)
public class FetchContactsUseCaseTest {

    public static final String FILTER = "filter";
    public static final int AGE = 20;
    public static final String URL = "url";
    public static final String PHONE_NUMBER = "phone number";
    public static final String FULL_NAME = "full name";
    public static final String ID = "id";

    @Mock
    GetContactsHttpEndpoint getContactsHttpEndpointMock;
    @Mock
    FetchContactsUseCase.Listener listener1;
    @Mock
    FetchContactsUseCase.Listener listener2;

    @Captor
    ArgumentCaptor<List<FailReason>> acFailReason;
    @Captor
    ArgumentCaptor<List<Contact>> acContact;

    FetchContactsUseCase SUT;

    @Before
    public void setUp() throws Exception {
        SUT = new FetchContactsUseCase(getContactsHttpEndpointMock);
        success();
    }

    @Test
    public void fetchContact_correctFilterValuePassed() {
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);

        SUT.fetchContactsAndNotify(FILTER);

        verify(getContactsHttpEndpointMock).getContacts(argumentCaptor.capture(), any(Callback.class));
        assertThat(argumentCaptor.getValue(), is(FILTER));
    }

    @Test
    public void fetchContact_success_registeredListenersNotified() {
        SUT.registerListeners(listener1);
        SUT.registerListeners(listener2);
        SUT.fetchContactsAndNotify(FILTER);

        verify(listener1).onContactsFetched(acContact.capture());
        verify(listener2).onContactsFetched(acContact.capture());

        List<List<Contact>> captures = acContact.getAllValues();
        List<Contact> capture1 = captures.get(0);
        List<Contact> capture2 = captures.get(1);

        assertThat(capture1, is(getContacts()));
        assertThat(capture2, is(getContacts()));
    }

    @Test
    public void fetchContact_success_unregisteredListenersAreNotNotified() {
        SUT.registerListeners(listener1);
        SUT.registerListeners(listener2);
        SUT.unregisterListers(listener2);
        SUT.fetchContactsAndNotify(FILTER);

        verifyZeroInteractions(listener2);
    }

    @Test
    public void fetchContact_generalFailure_generalFailureReturned() {
        generalFailure();
        ArgumentCaptor<FailReason> argumentCaptor = ArgumentCaptor.forClass(FailReason.class);
        SUT.registerListeners(listener1);
        SUT.registerListeners(listener2);

        SUT.fetchContactsAndNotify(FILTER);

        verify(listener1).onContactsFailure(argumentCaptor.capture());
        verify(listener2).onContactsFailure(argumentCaptor.capture());

        List<FailReason> capture1 = argumentCaptor.getAllValues();

        assertThat(capture1.get(0), is(FailReason.GENERAL_ERROR));
        assertThat(capture1.get(1), is(FailReason.GENERAL_ERROR));

    }

    @Test
    public void fetchContact_generalFailure_observersNotInvoked() {
        generalFailure();

        SUT.fetchContactsAndNotify(FILTER);

        verifyZeroInteractions(listener1);
        verifyZeroInteractions(listener2);
    }

    @Test
    public void fetchContact_networkFailure_observersNotInvoked() {
        networkFailure();

        SUT.fetchContactsAndNotify(FILTER);

        verifyZeroInteractions(listener1);
        verifyZeroInteractions(listener2);
    }

    @Test
    public void fetchContact_networkFailure_NetworkFailureReturned() {
        networkFailure();
        ArgumentCaptor<FailReason> argumentCaptor = ArgumentCaptor.forClass(FailReason.class);

        SUT.registerListeners(listener1);
        SUT.registerListeners(listener2);

        SUT.fetchContactsAndNotify(FILTER);

        verify(listener1).onContactsFailure(argumentCaptor.capture());
        verify(listener2).onContactsFailure(argumentCaptor.capture());

        List<FailReason> capture1 = argumentCaptor.getAllValues();

        assertThat(capture1.get(0), is(FailReason.NETWORK_ERROR));
        assertThat(capture1.get(1), is(FailReason.NETWORK_ERROR));

    }


    /*
     * Helper Method
     * */
    private void success() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                Callback callback = (Callback) args[1];
                callback.onGetContactsSucceeded(getContactsSchema());
                return null;
            }
        }).when(getContactsHttpEndpointMock).getContacts(anyString(), any(Callback.class));
    }


    private void generalFailure() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arg = invocation.getArguments();
                Callback callback = (Callback) arg[1];
                callback.onGetContactsFailed(FailReason.GENERAL_ERROR);
                return null;
            }
        }).when(getContactsHttpEndpointMock).getContacts(anyString(), any(Callback.class));
    }

    private void networkFailure() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arg = invocation.getArguments();
                Callback callback = (Callback) arg[1];
                callback.onGetContactsFailed(FailReason.NETWORK_ERROR);
                return null;
            }
        }).when(getContactsHttpEndpointMock).getContacts(anyString(), any(Callback.class));
    }

    private List<Contact> getContacts() {
        List<Contact> contacts = new ArrayList<>();
        contacts.add(new Contact(ID, FULL_NAME, URL));
        return contacts;
    }

    private List<ContactSchema> getContactsSchema() {
        List<ContactSchema> contactSchemas = new ArrayList<>();
        contactSchemas.add(new ContactSchema(ID, FULL_NAME, PHONE_NUMBER, URL, AGE));
        return contactSchemas;
    }

}