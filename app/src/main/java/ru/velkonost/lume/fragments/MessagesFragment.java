package ru.velkonost.lume.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ru.velkonost.lume.R;
import ru.velkonost.lume.adapter.MessageListAdapter;
import ru.velkonost.lume.descriptions.Message;

public class MessagesFragment extends Fragment {
    private static final int LAYOUT = R.layout.fragment_messages;

    private List<Message> mMessages;
    private MessageListAdapter adapter;
    protected View view;
    protected Context context;

    public static MessagesFragment getInstance(Context context, List<Message> messages) {
        Bundle args = new Bundle();
        MessagesFragment fragment = new MessagesFragment();

        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setMessages(messages);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewMessages);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        adapter = new MessageListAdapter(getActivity(), mMessages);
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(adapter.getItemCount()-1);

        return view;
    }

    public void refreshMessages(List<Message> mMessage) {
        adapter.setData(mMessage);
        adapter.notifyDataSetChanged();
    }

    public void setMessages(List<Message> messages) {
        mMessages = messages;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
