package com.stefankussmaul.activitylog.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.stefankussmaul.activitylog.R;
import com.stefankussmaul.activitylog.content.CheckList;
import com.stefankussmaul.activitylog.content.CheckListItem;

/**
 * Allows a CheckList to be edited. Displays each item and provides an entry for the user to enter
 * new items.
 */

public class CheckListEditorFragment extends Fragment {

    private EditText newItemEntry;
    private Button addItemBtn;

    private CheckList checkList;

    @Override  // TODO: TAKE INITIAL ITEMS FROM A checklist toString() IN BUNDLE
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkList = new CheckList();
        // TODO: READ IN ITEMS FROM BUNDLE

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.checklist_editor_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View view = getView();

        newItemEntry = (EditText) view.findViewById(R.id.checklist_add_entry);
        addItemBtn = (Button) view.findViewById(R.id.checklist_add_entry);

        addItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddItem(view);
            }
        });
    }

    // handles user adding a new entry to the CheckList through the newItemEntry field
    // makes sure it has text, add it as a new item, and clear the field
    public void onAddItem(View view) {
        String item_name = newItemEntry.getText().toString();
        if (!item_name.isEmpty()) {
            checkList.addItem(new CheckListItem(item_name));
            newItemEntry.setText("");
        }
    }
}
