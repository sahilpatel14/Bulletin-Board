package in.sahilpatel.bulletinboard.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import in.sahilpatel.bulletinboard.R;


public class EmptyThreadFragment extends Fragment {

    private String title;

    public EmptyThreadFragment() { }

    public static EmptyThreadFragment newInstance(String title) {
        EmptyThreadFragment f = new EmptyThreadFragment();
        f.setTitle(title);
        return f;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_empty_thread, container, false);

        TextView textView = (TextView)rootView.findViewById(R.id.empty_fragment_name);
        textView.setText(this.title);

        return rootView;
    }
}
