package com.xsolla.android.store.entity.request.inventory;

import java.util.List;

public class RevokeItemsRequest {

    private User user;
    private String comment;
    private List<Item> items;

    private class User {
        private String id;
    }

    private class Item {
        private String sku;
        private int quantity;
    }

}
