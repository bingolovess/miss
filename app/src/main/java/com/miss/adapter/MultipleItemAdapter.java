package com.miss.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.miss.R;
import com.miss.bean.MultipleItem;

public class MultipleItemAdapter extends BaseMultiAdapter<MultipleItem> {

    public MultipleItemAdapter(Context context) {
        super(context);
        addItemType(MultipleItem.TEXT, R.layout.list_item_text);
        addItemType(MultipleItem.IMG, R.layout.list_item_pic);
    }

    @Override
    public void onBindItemHolder(SuperViewHolder holder, int position) {
        MultipleItem item = getDataList().get(position);
        switch (item.getItemType()) {
            case MultipleItem.TEXT:
                bindTextItem(holder,item);
                break;
            case MultipleItem.IMG:
                bindPicItem(holder,item);
                break;
            default:
                break;
        }
    }

    private void bindTextItem(SuperViewHolder holder, MultipleItem item) {
        TextView textView = holder.getView(R.id.info_text);
        textView.setText(item.getTitle());
    }

    private void bindPicItem(SuperViewHolder holder, MultipleItem item) {
        TextView textView = holder.getView(R.id.info_text);
        ImageView avatarImage = holder.getView(R.id.avatar_image);

        textView.setText(item.getTitle());
        avatarImage.setImageResource(R.mipmap.logo);
    }
}
