package in.sahilpatel.bulletinboard.activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import in.sahilpatel.bulletinboard.R;

public class CustomPhotoGalleryActivity extends AppCompatActivity {

    private RecyclerView galleryRecyclerView;
    private FloatingActionButton btnSelect;

    private GalleryAdapter galleryAdapter;
    private String[] arrPath;
    private boolean[] thumbnailSelection;
    private int ids[];
    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_photo_gallery);

        btnSelect= (FloatingActionButton) findViewById(R.id.btnSelect);

        galleryRecyclerView = (RecyclerView)findViewById(R.id.gallery_recycler_view);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),3);
        //RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(COLUMN_COUNT,StaggeredGridLayoutManager.VERTICAL);
        //layoutManager = new StaggeredGridLayoutManager(3,0);
        galleryRecyclerView.setLayoutManager(layoutManager);
        galleryRecyclerView.setItemAnimator(new DefaultItemAnimator());


        final String[] columns = { MediaStore.Images.Media.DATA,MediaStore.Images.Media.DATE_TAKEN, MediaStore.Images.Media._ID };
        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;



        Cursor imageCursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,columns,null,null,orderBy+" DESC LIMIT 50");
        int image_column_index = imageCursor.getColumnIndex(MediaStore.Images.Media._ID);
        this.count = imageCursor.getCount();
        this.arrPath = new String[this.count];
        ids = new int[count];
        this.thumbnailSelection = new boolean[this.count];
        for (int i = 0; i < this.count; i++) {
            imageCursor.moveToPosition(i);
            ids[i] = imageCursor.getInt(image_column_index);
            int dataColumnIndex = imageCursor.getColumnIndex(MediaStore.Images.Media.DATA);
            arrPath[i] = imageCursor.getString(dataColumnIndex);
        }

        galleryAdapter = new GalleryAdapter();
        galleryRecyclerView.setAdapter(galleryAdapter);
        imageCursor.close();


        btnSelect.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                final int len = thumbnailSelection.length;
                int cnt = 0;
                String selectImages = "";
                for (int i = 0; i < len; i++) {
                    if (thumbnailSelection[i]) {
                        cnt++;
                        selectImages = selectImages + arrPath[i] + "|";
                    }
                }
                if (cnt == 0) {
                    Toast.makeText(getApplicationContext(), "Please select at least one image", Toast.LENGTH_LONG).show();
                } else {

                    Log.d("SelectedImages", selectImages);
                    Intent i = new Intent();
                    i.putExtra("data", selectImages);
                    setResult(RESULT_OK, i);
                    finish();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();

    }

    /**
     * Class method
     */

    /**
     * This method used to set bitmap.
     * @param iv represented ImageView
     * @param id represented id
     */

    private void setBitmap(final ImageView iv, final int id) {

        new AsyncTask<Void, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Void... params) {
                return MediaStore.Images.Thumbnails.getThumbnail(getApplicationContext().getContentResolver(), id, MediaStore.Images.Thumbnails.MICRO_KIND, null);
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                super.onPostExecute(result);
                iv.setImageBitmap(result);
            }
        }.execute();
    }


    public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.MyViewHolder> {



        public GalleryAdapter() {}

        /**
         *
         * Creates a new View and returns it as our ViewHolder.
         * Inflates a view inside the parent RecyclerView using the layout
         * of the item and its parent.
         *
         * Once created, it returns that view wrapped inside our ViewHolder
         *
         * @param parent
         * @param viewType
         * @return
         */
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_gallery_item,parent,false);
            return new MyViewHolder(itemView);
        }

        /**
         * The method is passed the viewHolder where we want to display the data
         * and index of data that we need to display.
         *
         * Get the image to be displayed.
         * display it in ViewHolder's imageView.
         * @param holder
         * @param position
         */
        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {

            holder.chkImage.setId(position);
            holder.imgThumb.setId(position);

            holder.chkImage.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    int id = cb.getId();
                    if (thumbnailSelection[id]) {
                        cb.setChecked(false);
                        thumbnailSelection[id] = false;
                    } else {
                        cb.setChecked(true);
                        thumbnailSelection[id] = true;
                    }
                }
            });
            holder.imgThumb.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    int id = holder.chkImage.getId();
                    if (thumbnailSelection[id]) {
                        holder.chkImage.setChecked(false);
                        thumbnailSelection[id] = false;
                    } else {
                        holder.chkImage.setChecked(true);
                        thumbnailSelection[id] = true;
                    }
                }
            });

            try {
                setBitmap(holder.imgThumb, ids[position]);
            } catch (Throwable e) {
            }
            holder.chkImage.setChecked(thumbnailSelection[position]);
            holder.id = position;
        }

        @Override
        public int getItemCount() {
            return count;
        }

        /**
         * ViewHolder class, created for every view that is created in onCreateViewHolder
         * We initialize the View where we intent to put image and that's it.
         */

        public class MyViewHolder extends RecyclerView.ViewHolder{
            ImageView imgThumb;
            CheckBox chkImage;
            int id;

            public MyViewHolder(View view) {
                super(view);
                imgThumb = (ImageView)view.findViewById(R.id.imgThumb);
                chkImage = (CheckBox)view.findViewById(R.id.chkImage);
            }
        }
    }
}
