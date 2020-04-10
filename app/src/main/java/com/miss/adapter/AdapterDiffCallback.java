package com.miss.adapter;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

/**
 * DiffUtil.Callback  用于计算recyclerView 的变化
 */
public class AdapterDiffCallback extends DiffUtil.Callback {

    private List<?> mOldList;
    private List<?> mNewList;

    public AdapterDiffCallback( List<?> oldList,List<?> newList){
        this.mOldList = oldList;
        this.mNewList = newList;
    }

    @Override
    public int getOldListSize() {
        return mOldList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldList.get(oldItemPosition).getClass().equals(mNewList.get(newItemPosition).getClass());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Object oldStr = mOldList.get(oldItemPosition);
        Object newStr = mNewList.get(newItemPosition);
        return oldStr.equals(newStr);
    }
}
