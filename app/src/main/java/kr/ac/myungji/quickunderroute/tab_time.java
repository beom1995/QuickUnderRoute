package kr.ac.myungji.quickunderroute;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class tab_time extends Fragment {
    TextView infoTime1, infoFare1, infoTrans1;
    String time, fare, trans;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tab_time, container, false);

        infoTime1 = (TextView) v.findViewById(R.id.info_time1);
        infoFare1 = (TextView) v.findViewById(R.id.info_fare1);
        infoTrans1 = (TextView) v.findViewById(R.id.info_trans1);

        Bundle bundle = getArguments();
        if (bundle != null) {
            time = bundle.getString("time");
            fare = bundle.getString("fare");
            trans = bundle.getString("trans");
            infoTime1.setText(time);
            infoFare1.setText(fare);
            infoTrans1.setText(trans);
        }

        return v;
    }
}
