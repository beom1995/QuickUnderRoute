package kr.ac.myungji.quickunderroute;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class tab_fare extends Fragment {
    TextView infoTime3, infoFare3, infoTrans3;
    String time, fare, trans;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tab_fare, container, false);

        infoTime3 = (TextView) v.findViewById(R.id.info_time3);
        infoFare3 = (TextView) v.findViewById(R.id.info_fare3);
        infoTrans3 = (TextView) v.findViewById(R.id.info_trans3);

        Bundle bundle = getArguments();
        if (bundle != null) {
            time = bundle.getString("time");
            fare = bundle.getString("fare");
            trans = bundle.getString("trans");
            infoTime3.setText(time);
            infoFare3.setText(fare);
            infoTrans3.setText(trans);
        }

        return v;
    }
}

