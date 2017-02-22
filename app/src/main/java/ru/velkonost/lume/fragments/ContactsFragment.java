package ru.velkonost.lume.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.velkonost.lume.R;
import ru.velkonost.lume.activity.SearchActivity;
import ru.velkonost.lume.adapter.ContactListAdapter;
import ru.velkonost.lume.descriptions.Contact;

import static ru.velkonost.lume.Constants.SEARCH;

public class ContactsFragment extends Fragment {
    private static final int LAYOUT = R.layout.fragment_contact;

    private List<Contact> mContacts;
    private List<Contact> mContactsCopy;
    private ContactListAdapter adapter;
    protected View view;
    protected Context context;

    private FloatingActionButton fabGoSearch;

    public static ContactsFragment getInstance(Context context, List<Contact> contacts) {
        Bundle args = new Bundle();
        ContactsFragment fragment = new ContactsFragment();

        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setContacts(contacts);
        fragment.setContactsCopy();

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);

        FastScrollRecyclerView recyclerView = (FastScrollRecyclerView)
                view.findViewById(R.id.recycleViewContact);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        adapter = new ContactListAdapter(getActivity(), mContacts);
        recyclerView.setAdapter(adapter);

        fabGoSearch = (FloatingActionButton) view.findViewById(R.id.btnGoSearch);
        fabGoSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);

                LinearLayout.LayoutParams  params =
                        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(dp2px(5), dp2px(20), dp2px(5), dp2px(20));

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(getResources().getString(R.string.go_search));

                final EditText inputName
                        = (EditText) getLayoutInflater(savedInstanceState)
                        .inflate(R.layout.item_edittext_style, null);
                inputName.setTextColor(ContextCompat.getColor(context, R.color.colorBlack));
                inputName.setLayoutParams(params);

                inputName.setHint(getResources().getString(R.string.enter_name_login_surname));
                inputName.setInputType(InputType.TYPE_CLASS_TEXT);
                layout.addView(inputName);

                builder.setView(layout)
                        .setPositiveButton(getResources().getString(R.string.search_empty),
                                new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (inputName.getText().toString().length() != 0) {

                                    Intent intent = new Intent(context, SearchActivity.class);
                                    intent.putExtra(SEARCH, inputName.getText().toString());

                                    context.startActivity(intent);

                                    getActivity().overridePendingTransition(R.anim.activity_right_in,
                                            R.anim.activity_diagonaltranslate);

                                } else dialog.cancel();

                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.cancel),
                                new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });


                AlertDialog alert = builder.create();
                alert.show();



            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if (dy > 0) fabGoSearch.hide();
                else if (dy < 0) fabGoSearch.show();
            }
        });

        final MaterialSearchView searchView = (MaterialSearchView) getActivity().findViewById(R.id.search_view);

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown () {
                searchView.setVisibility(View.VISIBLE);
                fabGoSearch.hide();
            }
            @Override
            public void onSearchViewClosed() {
                fabGoSearch.show();
            }
        });

        return view;
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

    public void search(String text, boolean empty, boolean let) {
        if (let) {
            if (!text.isEmpty()) {

                mContacts.clear();
                text = text.toLowerCase();

                for (Contact item : mContactsCopy)
                    if (item.getName().toLowerCase().contains(text)
                            || item.getSurname().toLowerCase().contains(text)
                            || item.getLogin().toLowerCase().contains(text))
                        mContacts.add(item);

                adapter.setData(mContacts);
            } else if (empty) {
                mContacts.clear();
                mContacts.addAll(mContactsCopy);
                adapter.setData(mContacts);
            }
        }
    }

    public void setContext (Context context) {this.context = context;}

    public void setContacts(List<Contact> mContacts) {
        this.mContacts = mContacts;
    }
    public void setContactsCopy() {
        mContactsCopy = new ArrayList<>();
        mContactsCopy.addAll(mContacts);
    }
}
