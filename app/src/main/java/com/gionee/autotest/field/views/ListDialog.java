package com.gionee.autotest.field.views;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.gionee.autotest.field.R;

import java.util.Arrays;
import java.util.List;

public class ListDialog extends AbsDialog<ListDialog> {

    private ListView choicesList;

    public ListDialog(Context context) {
        super(context);
    }

    public ListDialog(Context context, int theme) {
        super(context, theme);
    }

    {
        choicesList = findView(R.id.ld_choices);
    }

    public <T> ListDialog setItems(T[] items, OnItemSelectedListener<T> itemSelectedListener) {
        return setItems(Arrays.asList(items), itemSelectedListener);
    }

    public <T> ListDialog setItems(List<T> items, OnItemSelectedListener<T> itemSelectedListener) {
        ArrayAdapter<T> adapter = new ArrayAdapter<>(getContext(),
                R.layout.item_simple_text, android.R.id.text1,
                items);
        return setItems(adapter, itemSelectedListener);
    }

    public <T> ListDialog setItems(ArrayAdapter<T> adapter, OnItemSelectedListener<T> itemSelectedListener) {
        choicesList.setOnItemClickListener(new ItemSelectedAdapter<>(itemSelectedListener));
        choicesList.setAdapter(adapter);
        return this;
    }

    @Override
    protected int getLayout() {
        return R.layout.dialog_list;
    }

    private class ItemSelectedAdapter<T> implements AdapterView.OnItemClickListener {

        private OnItemSelectedListener<T> adaptee;

        private ItemSelectedAdapter(OnItemSelectedListener<T> adaptee) {
            this.adaptee = adaptee;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (adaptee != null) {
                adaptee.onItemSelected(position, (T) parent.getItemAtPosition(position));
            }
            dismiss();
        }
    }

    public interface OnItemSelectedListener<T> {
        void onItemSelected(int position, T item);
    }

}
