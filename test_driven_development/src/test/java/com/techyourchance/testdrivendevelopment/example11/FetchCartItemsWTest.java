package com.techyourchance.testdrivendevelopment.example11;

import com.techyourchance.testdrivendevelopment.example11.cart.CartItem;
import com.techyourchance.testdrivendevelopment.example11.networking.CartItemSchema;
import com.techyourchance.testdrivendevelopment.example11.networking.GetCartItemsHttpEndpoint;
import com.techyourchance.testdrivendevelopment.example11.networking.GetCartItemsHttpEndpoint.Callback;

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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class FetchCartItemsWTest {

    public static final String DESCRIPTION = "description";
    public static final String TITLE = "title";
    public static final String ID = "id";
    public static final int PRICE = 5;
    private static int LIMIT = 10;

    @Mock
    GetCartItemsHttpEndpoint getCartItemsHttpEndpoint;
    @Mock
    FetchCartItemsW.Listener listener1;
    @Mock
    FetchCartItemsW.Listener listener2;
    @Captor ArgumentCaptor<List<CartItem>> mACListCartItem;
    FetchCartItemsW SUT;


    @Before
    public void setUp() throws Exception {
        SUT = new FetchCartItemsW(getCartItemsHttpEndpoint);
        success();
    }

    private void success() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                Callback callback = (Callback) args[1];
                callback.onGetCartItemsSucceeded(getCartItemSchemes());
                return null;
            }
        }).when(getCartItemsHttpEndpoint).getCartItems(anyInt(), any(Callback.class));
    }

    private List<CartItemSchema> getCartItemSchemes() {
        List<CartItemSchema> schemas = new ArrayList<>();
        schemas.add(new CartItemSchema(ID, TITLE, DESCRIPTION, PRICE));
        return schemas;
    }

    //correct limit passed to endpoint

    @Test
    public void fetchCartItems_correctLimitPassedToEndpoint() {
        ArgumentCaptor<Integer> acInt = ArgumentCaptor.forClass(Integer.class);
        SUT.fetchCartItemsAndNotify(LIMIT);
        verify(getCartItemsHttpEndpoint).getCartItems(acInt.capture(), any(Callback.class));
        assertThat(acInt.getValue(), is(LIMIT));
    }

    //success - all observers notified with correct data

    @Test
    public void fetchCartItems_success_observersNotifiedWithCorrectData() {

        SUT.registerLitenerMethod(listener1);
        SUT.registerLitenerMethod(listener2);
        SUT.fetchCartItemsAndNotify(LIMIT);

        verify(listener1).onCartItemsFetched(mACListCartItem.capture());
        verify(listener2).onCartItemsFetched(mACListCartItem.capture());
        List<List<CartItem>> captures = mACListCartItem.getAllValues();
        List<CartItem> capture1 = captures.get(0);
        List<CartItem> capture2 = captures.get(1);
        assertThat(capture1, is(getCartItems()));
        assertThat(capture2, is(getCartItems()));
    }

    private List<CartItem> getCartItems() {
        List<CartItem> cartItems = new ArrayList<>();
        cartItems.add(new CartItem(ID, TITLE, DESCRIPTION, PRICE));
        return cartItems;
    }

    //success - unsubscribed observers not notified
    //general error - observers notified of errors
    //network error - observers notified of errors
}