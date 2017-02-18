package ru.velkonost.lume.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ru.velkonost.lume.R;
import ru.velkonost.lume.descriptions.BoardParticipant;

public class CardParticipantsFragment extends Fragment {
    private static final int LAYOUT = R.layout.fragment_board_participants;

    private List<BoardParticipant> mBoardsParticipants;
    protected View view;
    protected Context context;

    public static CardParticipantsFragment getInstance(Context context, List<BoardParticipant> participants) {
        Bundle args = new Bundle();
        CardParticipantsFragment fragment = new CardParticipantsFragment();

        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setParticipants(participants);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);

//        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.llBoardParticipants);
//
//        linearLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(context, CardParticipantsActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.putExtra(BOARD_ID, mBoardsParticipants.get(0).getBoardId());
//                context.startActivity(intent);
//            }
//        });
//
//        LayoutInflater inflaterous = LayoutInflater.from(context);
//
//        for (final BoardParticipant item : mBoardsParticipants) {
//            final View viewItem = inflaterous.inflate(R.layout.item_board_participant, linearLayout, false);
//
//            if (item.isLast()) {
//                ((TextView) viewItem.findViewById(R.id.another_participants))
//                        .setText(String.valueOf(PLUS + item.getReminded()));
//                viewItem.findViewById(R.id.another_participants).setVisibility(View.VISIBLE);
//
//                Bitmap bitmap = ((BitmapDrawable) ((ImageView) viewItem.findViewById(R.id.avatar))
//                        .getDrawable()).getBitmap();
//
//                ((ImageView) viewItem.findViewById(R.id.avatar))
//                        .setImageBitmap(getCircleMaskedBitmap(bitmap, 25));
//
//                viewItem.findViewById(R.id.avatar).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Intent intent = new Intent(context, CardParticipantsActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        intent.putExtra(BOARD_ID, item.getBoardId());
//                        context.startActivity(intent);
//                    }
//                });
//
//                linearLayout.addView(viewItem);
//
//                break;
//            }
//
//            /** Формирование адреса, по которому лежит аватар пользователя */
//            String avatarURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_RESOURCE
//                    + SERVER_AVATAR + SLASH + item.getAvatar()
//                    + SLASH + item.getId() + JPG;
//
//            ((TextView) viewItem.findViewById(R.id.userId)).setText(String.valueOf(item.getId()));
//
//            viewItem.findViewById(R.id.avatar).setBackgroundResource(0);
//            fetchImage(avatarURL, (ImageView) viewItem.findViewById(R.id.avatar), true, false);
//
//            Bitmap bitmap = ((BitmapDrawable) ((ImageView) viewItem.findViewById(R.id.avatar))
//                    .getDrawable()).getBitmap();
//            ((ImageView) viewItem.findViewById(R.id.avatar))
//                    .setImageBitmap(getCircleMaskedBitmap(bitmap, 25));
//
//            viewItem.findViewById(R.id.avatar).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent intent = new Intent(context, ProfileActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    intent.putExtra(ID, item.getId());
//                    context.startActivity(intent);
//                }
//            });
//
//            linearLayout.addView(viewItem);
//        }

        return view;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setParticipants(List<BoardParticipant> mParticipants) {
        this.mBoardsParticipants = mParticipants;
    }
}