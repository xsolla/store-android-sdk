package com.xsolla.android.store.entity.request.items;

import java.util.List;

public class ItemsRequestOptions {

    private Integer limit = null;
    private String externalId = null;
    private Integer offset = null;
    private String locale = null;
    private List<String> additionalFields = null;

    public Integer getLimit() {
        return limit;
    }

    public String getExternalId() {
        return externalId;
    }

    public Integer getOffset() {
        return offset;
    }

    public String getLocale() {
        return locale;
    }

    public List<String> getAdditionalFields() {
        return additionalFields;
    }

    public Builder create() {
        return new ItemsRequestOptions().new Builder();
    }

    public class Builder {

        private Builder() {

        }

        public Builder setLimit(int limit) {
            ItemsRequestOptions.this.limit = limit;
            return this;
        }

        public Builder setExternalId(String externalId) {
            ItemsRequestOptions.this.externalId = externalId;
            return this;
        }

        public Builder setOffset(int offset) {
            ItemsRequestOptions.this.offset = offset;
            return this;
        }

        public Builder setLocale(String locale) {
            ItemsRequestOptions.this.locale = locale;
            return this;
        }

        public Builder setAdditionalFields(List<String> additionalFields) {
            ItemsRequestOptions.this.additionalFields = additionalFields;
            return this;
        }

        public ItemsRequestOptions build() {
            return ItemsRequestOptions.this;
        }

    }
}
