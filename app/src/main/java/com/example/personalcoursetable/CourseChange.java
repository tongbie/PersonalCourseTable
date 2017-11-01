package com.example.personalcoursetable;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CourseChange extends AppCompatActivity implements View.OnClickListener {
    private EditText edit_coursename;
    private EditText edit_courseweek;
    private EditText edit_courseplace;
    private EditText edit_courseteacher;
    private String[] courseInformation=new String[4];
    private Button edit_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_change);
        for(int i=0;i<4;i++){
            courseInformation[i]="";
        }
        edit_coursename=(EditText)findViewById(R.id.edit_coursename);
        edit_courseweek=(EditText)findViewById(R.id.edit_courseweek);
        edit_courseplace=(EditText)findViewById(R.id.edit_courseplace);
        edit_courseteacher=(EditText)findViewById(R.id.edit_courseteacher);
        edit_button=(Button)findViewById(R.id.edit_button);
        edit_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        courseInformation[0]=edit_coursename.getText().toString();
        courseInformation[1]=edit_courseweek.getText().toString();
        courseInformation[2]=edit_courseplace.getText().toString();
        courseInformation[3]=edit_courseteacher.getText().toString();
        Intent intent=new Intent();
        Bundle bundle=new Bundle();
        bundle.putStringArray("return_data",courseInformation);
        intent.putExtras(bundle);
        finish();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent=new Intent();
        Bundle bundle=new Bundle();
        bundle.putStringArray("return_data",courseInformation);
        intent.putExtras(bundle);
        finish();
    }

    public String getCourseInformation(int stringNumber){
        return courseInformation[stringNumber];
    }
}
