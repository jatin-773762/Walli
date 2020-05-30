package com.example.walli.Helper;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.walli.R;

public class GenderDialog extends DialogFragment {
    private static final String TAG = "GenderDialog";
    private ImageButton button;
    private String input;
    private RadioButton r1,r2,r3;

    public interface OnInputListener{
        void sendInput(String input);
    }
    private OnInputListener mOnInputListener;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gender_dialog,container,false);

        r1 = view.findViewById(R.id.male_radio);
        r2 = view.findViewById(R.id.female_radio);
        r3 = view.findViewById(R.id.other_radio);
        button = view.findViewById(R.id.gender_submit);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(r1.isChecked()){
                    input = "M";
                    mOnInputListener.sendInput(input);
                }else if(r2.isChecked()){
                    input = "F";
                    mOnInputListener.sendInput(input);
                }
                else if(r3.isChecked()){
                    input = "O";
                    mOnInputListener.sendInput(input);
                }
                Log.d(TAG, "onClick: capturing input");
                getDialog().dismiss();
            }
        });
        return view;
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            mOnInputListener = (OnInputListener) getActivity();
        }catch (ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage() );
        }
    }
}
