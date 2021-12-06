package kr.ac.myungji.quickunderroute;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class tab_dist extends Fragment {
    TextView infoTime2, infoFare2, infoTrans2;
    String time, fare, trans;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tab_dist, container, false);

        infoTime2 = (TextView) v.findViewById(R.id.info_time2);
        infoFare2 = (TextView) v.findViewById(R.id.info_fare2);
        infoTrans2 = (TextView) v.findViewById(R.id.info_trans2);

        Bundle bundle = getArguments();
        if (bundle != null) {
            time = bundle.getString("time");
            fare = bundle.getString("fare");
            trans = bundle.getString("trans");
            infoTime2.setText(time);
            infoFare2.setText(fare);
            infoTrans2.setText(trans);
        }

        return v;
    }
}
