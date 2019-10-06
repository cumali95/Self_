package ui;


import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.self.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.annotation.RegEx;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import model.Journal;

public class JournalRecyclerAdapter extends RecyclerView.Adapter<JournalRecyclerAdapter.ViewHolder> {
    private Context context;
    private List<Journal> journalList;

    public JournalRecyclerAdapter(Context context, List<Journal> journalList) {
        this.context = context;
        this.journalList = journalList;
    }

    @NonNull
    @Override
    public JournalRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.journal_row,parent,false);
        return new ViewHolder(view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull JournalRecyclerAdapter.ViewHolder holder, int position) {

        Journal journal=journalList.get(position);
        String imageUrl;

        holder.thoughts.setText(journal.getThought());
        holder.name.setText(journal.getUserName());
        holder.title.setText(journal.getTitle());
        imageUrl=journal.getImageUrl();

        String timeAgo= (String) DateUtils.getRelativeTimeSpanString(journal.getTimeAdded().getSeconds()*1000);

        holder.dateAdded.setText(timeAgo);



        Picasso.get().load(imageUrl).placeholder(R.drawable.image_three).fit().into(holder.image);


    }

    @Override
    public int getItemCount() {
        return journalList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title,thoughts,dateAdded,name;
        public ImageView image;
        String userId;
        String username;
        public ImageButton shareButton;

        public ViewHolder(@NonNull View itemView,Context ctx) {
            super(itemView);
            context=ctx;
            title=itemView.findViewById(R.id.journal_title_list);
            thoughts=itemView.findViewById(R.id.journal_thoughts_list);
            dateAdded=itemView.findViewById(R.id.journal_timestam_list);
            image=itemView.findViewById(R.id.journal_image_list);
            name=itemView.findViewById(R.id.journal_row_username);
            shareButton=itemView.findViewById(R.id.journal_row_share_button);
            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        }
    }
}
