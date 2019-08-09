package com.techyourchance.testdrivendevelopment.example11;

import com.techyourchance.testdrivendevelopment.example11.cart.CartItem;
import com.techyourchance.testdrivendevelopment.example11.networking.CartItemSchema;
import com.techyourchance.testdrivendevelopment.example11.networking.GetCartItemsHttpEndpoint;

import java.util.ArrayList;
import java.util.List;

public class FetchCartItemsW {
    private final List<Listener> listeners = new ArrayList<>();
    private final GetCartItemsHttpEndpoint getCartItemsHttpEndpoint;

    public FetchCartItemsW(GetCartItemsHttpEndpoint getCartItemsHttpEndpoint) {
        this.getCartItemsHttpEndpoint = getCartItemsHttpEndpoint;
    }

    public void fetchCartItemsAndNotify(int limits) {

        getCartItemsHttpEndpoint.getCartItems(limits, new GetCartItemsHttpEndpoint.Callback() {
            @Override
            public void onGetCartItemsSucceeded(List<CartItemSchema> cartItems) {
                for (Listener listener : listeners) {
                    listener.onCartItemsFetched(cartItemsFromSchemas(cartItems));
                }
            }

            @Override
            public void onGetCartItemsFailed(GetCartItemsHttpEndpoint.FailReason failReason) {

            }
        });

    }

    private List<CartItem> cartItemsFromSchemas(List<CartItemSchema> cartItemSchemas) {
        List<CartItem> cartItems = new ArrayList<>();
        for(CartItemSchema schema : cartItemSchemas){
            cartItems.add(new CartItem(schema.getId(),schema.getTitle(),schema.getDescription(),schema.getPrice()));
        }
        return cartItems;
    }

    public void registerLitenerMethod(Listener listener) {
        listeners.add(listener);
    }

    public interface Listener {
        void onCartItemsFetched(List<CartItem> capture);
    }
}
