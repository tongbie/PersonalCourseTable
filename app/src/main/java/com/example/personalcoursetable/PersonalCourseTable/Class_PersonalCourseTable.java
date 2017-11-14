package com.example.personalcoursetable.PersonalCourseTable;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.personalcoursetable.R;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class Class_PersonalCourseTable extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {
    private Button button[][] = new Button[7][10];//Button数组
    private LinearLayout.LayoutParams layoutParams;//布局参数
    private int _2dp;//相邻Button间隔
    private LinearLayout[] day = new LinearLayout[7];//每天LinearLayout
    private int _0dp;
    private int _58dp;//按钮高度
    private int _256dp;//弹窗高度
    private String JsonString;//课表Json字符串
    private String[][] courseNameArray = new String[7][10];//课程名称
    private String[][] courseWeekArray = new String[7][10];//课程时间
    private String[][] coursePlaceArray = new String[7][10];//课程地点
    private String[][] courseTeacherArray = new String[7][10];//课程教师
    private int[][] courseLength = new int[7][10];//课程长度
    private int PERMISSION_WRITE_EXTERNAL_STORAGE = 0x001;//读写权限
    private File file;//手机存储文件
    private boolean havePermission = false;//判断权限
    private EditText editTextCourseName;//课程名称修改框
    private EditText editTextCourseWeek;//课程时间修改框
    private EditText editTextCoursePlace;//课程地点修改框
    private EditText editTextCourseTeacher;//课程教师修改框
    private Button popupButton;//“修改”按钮
    private ScrollView scrollView;//滚动布局，用以确定修改弹窗位置
    private LinearLayout popLinearLayout;//“修改”弹窗动态线性布局
    private PopupWindow popupWindow;//“修改”弹框
    private SwipeRefreshLayout swipeRefreshLayout;//下拉刷新

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personalcoursetable_activity);
        Window window = getWindow();
        window.setStatusBarColor(Color.parseColor("#7ebeab"));//设置状态栏颜色
        day[0] = (LinearLayout) findViewById(R.id.day0);
        day[1] = (LinearLayout) findViewById(R.id.day1);
        day[2] = (LinearLayout) findViewById(R.id.day2);
        day[3] = (LinearLayout) findViewById(R.id.day3);
        day[4] = (LinearLayout) findViewById(R.id.day4);
        day[5] = (LinearLayout) findViewById(R.id.day5);
        day[6] = (LinearLayout) findViewById(R.id.day6);
        _0dp = (int) getResources().getDimension(R.dimen._0dp);
        _58dp = (int) getResources().getDimension(R.dimen._58dp);//指定Button高度（res/values/dimens/...）
        _2dp = (int) getResources().getDimension(R.dimen._2dp);//相邻Button间隔
        _256dp = (int) getResources().getDimension(R.dimen._256dp);
        /*权限 ->*/
        if (ContextCompat.checkSelfPermission(Class_PersonalCourseTable.this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Class_PersonalCourseTable.this, new String[]{WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE_EXTERNAL_STORAGE);
        } else {
            havePermission = true;
        }/*<- 权限*/
        /*布局 ->*/
        layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, _58dp);//布局参数
        layoutParams.setMargins(_2dp, _2dp, 0, 0);//设置边距
        /*<- 布局*/
        /*文件读写 ->*/
        file = new File("/sdcard/CourseJsonData.txt");//文件位置
        if (file.exists() && havePermission) {
            JsonString = getJsonStringFromMemory();
        } else {
            JsonString = getLocalJson("coursetable_json.txt");//读取本地JSON
            saveData();//将JSON数据写入手机存储
        }/*<- 文件读写*/
        addJson();//将JSON中的null填充
        getCourseArray();//获取课表数据
        setCourseLength();//设置课程长度
        addLongCourse();//添加长Button
        initPopupWindow();//添加PopupWindow控件
        initRefresh();//添加下拉刷新
    }

    @Override/*按钮点击事件*/
    public void onClick(View view) {
        Button onClickButton = (Button) view;
        ButtonPosition buttonPosition = (ButtonPosition) onClickButton.getTag();
        int X = buttonPosition.x, Y = buttonPosition.y;
        if (!button[X][Y].getText().equals("")) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(Class_PersonalCourseTable.this);
            dialog.setTitle(courseNameArray[X][Y]);
            dialog.setMessage("课程时间：" + courseWeekArray[X][Y] + "\n上课地点：" + coursePlaceArray[X][Y] + "\n任课教师：" + courseTeacherArray[X][Y]);
            dialog.setCancelable(true);
            dialog.show();
        }
    }

    @Override/*按钮长按事件*/
    public boolean onLongClick(View view) {
        Button onClickButton = (Button) view;
        ButtonPosition buttonPosition = (ButtonPosition) onClickButton.getTag();
        int X = buttonPosition.x, Y = buttonPosition.y;
        showPopupMenu(button[X][Y], X, Y);
        return true;//返回值改为true，消费掉该事件，阻止事件向下传递
    }

    @Override/*权限回调方法*/
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(getApplicationContext(), "存储权限获取失败，无法修改课表", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*用以获取Button在数组中的坐标*/
    class ButtonPosition {
        int x, y;

        public ButtonPosition(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    /*添加Button*/
    @SuppressLint("WrongConstant")
    private void addButton() {
        LinearLayout.LayoutParams spaceLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, _58dp);//布局参数
        spaceLayoutParams.setMargins(_2dp, (int) getResources().getDimension(R.dimen._12dp), 0, 0);//设置边距
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 10; j++) {
                button[i][j] = new Button(this);
                button[i][j].setLayoutParams(layoutParams);//设置Button大小
                button[i][j].setTextAppearance(getApplicationContext(), R.style.Widget_AppCompat_Button_Borderless);
                button[i][j].setTag(new ButtonPosition(i, j));//设置坐标
                button[i][j].setTextColor(Color.parseColor("#ffffff"));//设置文字颜色
                button[i][j].setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);//设置文字大小
                button[i][j].setText(courseNameArray[i][j]);//设置文字
                if (j == 4 || j == 8) {//设置分割线
                    button[i][j].setLayoutParams(spaceLayoutParams);
                }
                button[i][j].setOnClickListener(this);//Button点击事件
                button[i][j].setOnLongClickListener(this);
                setButtonBackgroundColor(i,j);
                if (button[i][j].getText().equals("")) {//隐藏无内容Button
                    button[i][j].setBackgroundColor(Color.parseColor("#00000000"));
                }
                day[i].addView(button[i][j]);
            }
        }
    }

    /*展示选项卡*/
    private void showPopupMenu(View view, final int X, final int Y) {
        PopupMenu popupMenu = new PopupMenu(this, view);//View当前PopupMenu显示的相对View的位置
        popupMenu.getMenuInflater().inflate(R.menu.main, popupMenu.getMenu());//menu布局
        /*menu的item点击事件->*/
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getTitle().toString()) {
                    case "修改":
                        reviseJson(X, Y);
                        break;
                    case "删除":
                        deleteJson(X, Y);
                        break;
                    default:
                }
                return true;
            }
        });
        /*<-menu的item点击事件*/
        popupMenu.show();
    }

    /*本地读取JSON文件（main/assets/...）*/
    private String getLocalJson(String filename) {
        String JsonString = "";
        try {
            InputStream is = getAssets().open(filename);    //此处为要加载的json文件名称
            InputStreamReader reader = new InputStreamReader(is, "GBK");
            BufferedReader bufferedReader = new BufferedReader(reader);
            StringBuffer buffer = new StringBuffer("");
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
                buffer.append("\n");
            }
            JsonString = buffer.toString();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "getLocalJson(...)"+e.toString(), Toast.LENGTH_LONG).show();
        }
        return JsonString;
    }

    /*获取课表数据，置入courseNameArray*/
    private void getCourseArray() {
        int i;
        Gson gson = new Gson();
        Gson_PersonalCourseTable gsonPersonalCourseTable = gson.fromJson(JsonString, Gson_PersonalCourseTable.class);
        try {
            for (i = 0; i < 10; i++) {
                if (gsonPersonalCourseTable.getMondayCourse() != null) {
                    courseNameArray[0][i] = gsonPersonalCourseTable.getMondayCourse().get(i).getCourseName();
                    courseWeekArray[0][i] = gsonPersonalCourseTable.getMondayCourse().get(i).getCourseWeek();
                    coursePlaceArray[0][i] = gsonPersonalCourseTable.getMondayCourse().get(i).getCoursePlace();
                    courseTeacherArray[0][i] = gsonPersonalCourseTable.getMondayCourse().get(i).getCourseTeacher();
                }
                if (gsonPersonalCourseTable.getTuesdayCourse() != null) {
                    courseNameArray[1][i] = gsonPersonalCourseTable.getTuesdayCourse().get(i).getCourseName();
                    courseWeekArray[1][i] = gsonPersonalCourseTable.getTuesdayCourse().get(i).getCourseWeek();
                    coursePlaceArray[1][i] = gsonPersonalCourseTable.getTuesdayCourse().get(i).getCoursePlace();
                    courseTeacherArray[1][i] = gsonPersonalCourseTable.getTuesdayCourse().get(i).getCourseTeacher();
                }
                if (gsonPersonalCourseTable.getWednesdayCourse() != null) {
                    courseNameArray[2][i] = gsonPersonalCourseTable.getWednesdayCourse().get(i).getCourseName();
                    courseWeekArray[2][i] = gsonPersonalCourseTable.getWednesdayCourse().get(i).getCourseWeek();
                    coursePlaceArray[2][i] = gsonPersonalCourseTable.getWednesdayCourse().get(i).getCoursePlace();
                    courseTeacherArray[2][i] = gsonPersonalCourseTable.getWednesdayCourse().get(i).getCourseTeacher();
                }
                if (gsonPersonalCourseTable.getTuesdayCourse() != null) {
                    courseNameArray[3][i] = gsonPersonalCourseTable.getThursdayCourse().get(i).getCourseName();
                    courseWeekArray[3][i] = gsonPersonalCourseTable.getThursdayCourse().get(i).getCourseWeek();
                    coursePlaceArray[3][i] = gsonPersonalCourseTable.getThursdayCourse().get(i).getCoursePlace();
                    courseTeacherArray[3][i] = gsonPersonalCourseTable.getThursdayCourse().get(i).getCourseTeacher();
                }
                if (gsonPersonalCourseTable.getFridayCourse() != null) {
                    courseNameArray[4][i] = gsonPersonalCourseTable.getFridayCourse().get(i).getCourseName();
                    courseWeekArray[4][i] = gsonPersonalCourseTable.getFridayCourse().get(i).getCourseWeek();
                    coursePlaceArray[4][i] = gsonPersonalCourseTable.getFridayCourse().get(i).getCoursePlace();
                    courseTeacherArray[4][i] = gsonPersonalCourseTable.getFridayCourse().get(i).getCourseTeacher();
                }
                if (gsonPersonalCourseTable.getSaturdayCourse() != null) {
                    courseNameArray[5][i] = gsonPersonalCourseTable.getSaturdayCourse().get(i).getCourseName();
                    courseWeekArray[5][i] = gsonPersonalCourseTable.getSaturdayCourse().get(i).getCourseWeek();
                    coursePlaceArray[5][i] = gsonPersonalCourseTable.getSaturdayCourse().get(i).getCoursePlace();
                    courseTeacherArray[5][i] = gsonPersonalCourseTable.getSaturdayCourse().get(i).getCourseTeacher();
                }
                if (gsonPersonalCourseTable.getSundayCourse() != null) {
                    courseNameArray[6][i] = gsonPersonalCourseTable.getSundayCourse().get(i).getCourseName();
                    courseWeekArray[6][i] = gsonPersonalCourseTable.getSundayCourse().get(i).getCourseWeek();
                    coursePlaceArray[6][i] = gsonPersonalCourseTable.getSundayCourse().get(i).getCoursePlace();
                    courseTeacherArray[6][i] = gsonPersonalCourseTable.getSundayCourse().get(i).getCourseTeacher();
                }
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "getCourseArray()\n"+e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    /*将JSON写入手机存储*/
    private void saveData() {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);//创建向指定File对象中写入数据的文件输出流
            fileOutputStream.write((JsonString).getBytes());//将JsonString写入文件
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "saveData()\n" + e.toString(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "saveData()\n" + e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    /*从手机存储读取JSON*/
    private String getJsonStringFromMemory() {
        try {
            FileInputStream fin = new FileInputStream("/sdcard/CourseJsonData.txt");
            BufferedReader buffer = new BufferedReader(new InputStreamReader(fin));//把字节流转化为字符流
            String text = buffer.readLine();//读取文件
            return text;
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "getJsonStringFromMemory()/n" + e.toString(), Toast.LENGTH_SHORT).show();
        }
        return "";
    }

    /*修改JSON文件*/
    private void reviseJson(final int X, final int Y) {
        final Gson gson = new Gson();
        final Gson_PersonalCourseTable gsonPersonalCourseTable = gson.fromJson(JsonString, Gson_PersonalCourseTable.class);
        editTextCourseName.setText(courseNameArray[X][Y]);//显示课程名称
        editTextCourseName.setHint("课程名称：");//显示提示文字
        editTextCourseWeek.setText(courseWeekArray[X][Y]);//显示课程时间
        editTextCourseWeek.setHint("课程时间：");
        editTextCoursePlace.setText(coursePlaceArray[X][Y]);//显示课程地点
        editTextCoursePlace.setHint("上课地点：");
        editTextCourseTeacher.setText(courseTeacherArray[X][Y]);//显示任课教师
        editTextCourseTeacher.setHint("任课教师：");
        /*点击事件*/
        popupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setJson(gsonPersonalCourseTable, X, Y, editTextCourseName.getText().toString(), editTextCourseWeek.getText().toString(), editTextCoursePlace.getText().toString(), editTextCourseTeacher.getText().toString());
                JsonString = gson.toJson(gsonPersonalCourseTable);
                saveData();
                courseNameArray[X][Y] = editTextCourseName.getText().toString();
                courseWeekArray[X][Y] = editTextCourseWeek.getText().toString();
                coursePlaceArray[X][Y] = editTextCoursePlace.getText().toString();
                courseTeacherArray[X][Y] = editTextCourseTeacher.getText().toString();
                button[X][Y].setText(editTextCourseName.getText().toString());
                button[X][Y].setBackground(getDrawable(R.drawable.button_new));
                popupWindow.dismiss();
            }
        });
        popupWindow.showAtLocation(scrollView, Gravity.CENTER, 0, 0);
    }

    /*删除JSON文件*/
    private void deleteJson(int X, int Y) {
        courseNameArray[X][Y] = "";
        courseWeekArray[X][Y] = "";
        coursePlaceArray[X][Y] = "";
        courseTeacherArray[X][Y] = "";
        button[X][Y].setBackgroundColor(Color.parseColor("#00000000"));
        button[X][Y].setText("");
        Gson gson = new Gson();
        Gson_PersonalCourseTable gsonPersonalCourseTable = gson.fromJson(JsonString, Gson_PersonalCourseTable.class);
        setJson(gsonPersonalCourseTable, X, Y, "", "", "", "");
        JsonString = gson.toJson(gsonPersonalCourseTable);
        saveData();
    }

    /*修改和删除JSON文件的中间方法*/
    private void setJson(Gson_PersonalCourseTable gsonPersonalCourseTable, int X, int Y, String courseName, String courseWeek, String coursePlace, String courseTeacher) {
        try {
            switch (X) {
                case 0:
                    gsonPersonalCourseTable.getMondayCourse().get(Y).setCourseName(courseName);
                    gsonPersonalCourseTable.getMondayCourse().get(Y).setCoursePlace(coursePlace);
                    gsonPersonalCourseTable.getMondayCourse().get(Y).setCourseTeacher(courseTeacher);
                    gsonPersonalCourseTable.getMondayCourse().get(Y).setCourseWeek(courseWeek);
                    break;
                case 1:
                    gsonPersonalCourseTable.getTuesdayCourse().get(Y).setCourseName(courseName);
                    gsonPersonalCourseTable.getTuesdayCourse().get(Y).setCoursePlace(coursePlace);
                    gsonPersonalCourseTable.getTuesdayCourse().get(Y).setCourseTeacher(courseTeacher);
                    gsonPersonalCourseTable.getTuesdayCourse().get(Y).setCourseWeek(courseWeek);
                    break;
                case 2:
                    gsonPersonalCourseTable.getWednesdayCourse().get(Y).setCourseName(courseName);
                    gsonPersonalCourseTable.getWednesdayCourse().get(Y).setCoursePlace(coursePlace);
                    gsonPersonalCourseTable.getWednesdayCourse().get(Y).setCourseTeacher(courseTeacher);
                    gsonPersonalCourseTable.getWednesdayCourse().get(Y).setCourseWeek(courseWeek);
                    break;
                case 3:
                    gsonPersonalCourseTable.getThursdayCourse().get(Y).setCourseName(courseName);
                    gsonPersonalCourseTable.getThursdayCourse().get(Y).setCoursePlace(coursePlace);
                    gsonPersonalCourseTable.getThursdayCourse().get(Y).setCourseTeacher(courseTeacher);
                    gsonPersonalCourseTable.getThursdayCourse().get(Y).setCourseWeek(courseWeek);
                    break;
                case 4:
                    gsonPersonalCourseTable.getFridayCourse().get(Y).setCourseName(courseName);
                    gsonPersonalCourseTable.getFridayCourse().get(Y).setCoursePlace(coursePlace);
                    gsonPersonalCourseTable.getFridayCourse().get(Y).setCourseTeacher(courseTeacher);
                    gsonPersonalCourseTable.getFridayCourse().get(Y).setCourseWeek(courseWeek);
                    break;
                case 5:
                    gsonPersonalCourseTable.getSaturdayCourse().get(Y).setCourseName(courseName);
                    gsonPersonalCourseTable.getSaturdayCourse().get(Y).setCoursePlace(coursePlace);
                    gsonPersonalCourseTable.getSaturdayCourse().get(Y).setCourseTeacher(courseTeacher);
                    gsonPersonalCourseTable.getSaturdayCourse().get(Y).setCourseWeek(courseWeek);
                    break;
                case 6:
                    gsonPersonalCourseTable.getSundayCourse().get(Y).setCourseName(courseName);
                    gsonPersonalCourseTable.getSundayCourse().get(Y).setCoursePlace(coursePlace);
                    gsonPersonalCourseTable.getSundayCourse().get(Y).setCourseTeacher(courseTeacher);
                    gsonPersonalCourseTable.getSundayCourse().get(Y).setCourseWeek(courseWeek);
                    break;
                default:
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "setJson(...)\n" + e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    /*将JSON中的null填充*/
    private void addJson() {
        Gson gson = new Gson();
        Gson_PersonalCourseTable gsonPersonalCourseTable = gson.fromJson(JsonString, Gson_PersonalCourseTable.class);
        try {
            for (int i = 0; i < 10; i++) {
                if (gsonPersonalCourseTable.getMondayCourse() != null) {
                    if (gsonPersonalCourseTable.getMondayCourse().get(i) == null) {
                        gsonPersonalCourseTable.getMondayCourse().set(i, new Gson_PersonalCourseTable.DayCourse("", "", "", ""));
                    }
                } else {
                    gsonPersonalCourseTable.setMondayCourse(addArrayList());
                }
                if (gsonPersonalCourseTable.getTuesdayCourse() != null) {
                    if (gsonPersonalCourseTable.getTuesdayCourse().get(i) == null) {
                        gsonPersonalCourseTable.getTuesdayCourse().set(i, new Gson_PersonalCourseTable.DayCourse("", "", "", ""));
                    }
                } else {
                    gsonPersonalCourseTable.setTuesdayCourse(addArrayList());
                }
                if (gsonPersonalCourseTable.getWednesdayCourse() != null) {
                    if (gsonPersonalCourseTable.getWednesdayCourse().get(i) == null) {
                        gsonPersonalCourseTable.getWednesdayCourse().set(i, new Gson_PersonalCourseTable.DayCourse("", "", "", ""));
                    }
                } else {
                    gsonPersonalCourseTable.setWednesdayCourse(addArrayList());
                }
                if (gsonPersonalCourseTable.getThursdayCourse() != null) {
                    if (gsonPersonalCourseTable.getThursdayCourse().get(i) == null) {
                        gsonPersonalCourseTable.getThursdayCourse().set(i, new Gson_PersonalCourseTable.DayCourse("", "", "", ""));
                    }
                } else {
                    gsonPersonalCourseTable.setThursdayCourse(addArrayList());
                }
                if (gsonPersonalCourseTable.getFridayCourse() != null) {
                    if (gsonPersonalCourseTable.getFridayCourse().get(i) == null) {
                        gsonPersonalCourseTable.getFridayCourse().set(i, new Gson_PersonalCourseTable.DayCourse("", "", "", ""));
                    }
                } else {
                    gsonPersonalCourseTable.setFridayCourse(addArrayList());
                }
                if (gsonPersonalCourseTable.getSaturdayCourse() != null) {
                    if (gsonPersonalCourseTable.getSaturdayCourse().get(i) == null) {
                        gsonPersonalCourseTable.getSaturdayCourse().set(i, new Gson_PersonalCourseTable.DayCourse("", "", "", ""));
                    }
                } else {
                    gsonPersonalCourseTable.setSaturdayCourse(addArrayList());
                }
                if (gsonPersonalCourseTable.getSundayCourse() != null) {
                    if (gsonPersonalCourseTable.getSundayCourse().get(i) == null) {
                        gsonPersonalCourseTable.getSundayCourse().set(i, new Gson_PersonalCourseTable.DayCourse("", "", "", ""));
                    }
                } else {
                    ArrayList<Gson_PersonalCourseTable.DayCourse> arrayList = new ArrayList<Gson_PersonalCourseTable.DayCourse>();
                    for (int j = 0; j < 10; j++) {
                        arrayList.add(new Gson_PersonalCourseTable.DayCourse("", "", "", ""));
                    }
                    gsonPersonalCourseTable.setSundayCourse(addArrayList());
                }
            }
            JsonString = gson.toJson(gsonPersonalCourseTable);
            saveData();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "void getCourseArray()\n" + e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    /*addJson()中间方法，填充一天的数据*/
    private ArrayList<Gson_PersonalCourseTable.DayCourse> addArrayList() {
        ArrayList<Gson_PersonalCourseTable.DayCourse> arrayList = new ArrayList<Gson_PersonalCourseTable.DayCourse>();
        for (int j = 0; j < 10; j++) {
            arrayList.add(new Gson_PersonalCourseTable.DayCourse("", "", "", ""));
        }
        return arrayList;
    }

    /*添加修改弹窗*/
    private void initPopupWindow() {
        /*PopWindow ->*/
        editTextCourseName = new EditText(this);//CourseName输入框
        editTextCourseName.setWidth(_256dp);
        editTextCourseWeek = new EditText(this);//CourseWeek输入框
        editTextCourseWeek.setWidth(_256dp);
        editTextCoursePlace = new EditText(this);//CoursePlace输入框
        editTextCoursePlace.setWidth(_256dp);
        editTextCourseTeacher = new EditText(this);//CourseTeacher输入框
        editTextCourseTeacher.setWidth(_256dp);
        popupButton = new Button(this);//弹窗按钮
        popupButton.setBackgroundColor(Color.parseColor("#676767"));
        popupButton.setWidth(_256dp);
        popupButton.setText("保  存");
        popupButton.setTextColor(Color.parseColor("#ffffff"));
        popupButton.setTextSize(getResources().getDimension(R.dimen._8dp));
        /*<- PopWindow*/
        scrollView = (ScrollView) findViewById(R.id.scrollView);//用以确定popupWindow位置
        /*弹窗LinearLayout布局 ->*/
        popLinearLayout = new LinearLayout(this);
        popLinearLayout.setOrientation(1);//设置LinearLayout纵向
        popLinearLayout.addView(editTextCourseName);
        popLinearLayout.addView(editTextCourseWeek);
        popLinearLayout.addView(editTextCoursePlace);
        popLinearLayout.addView(editTextCourseTeacher);
        popLinearLayout.addView(popupButton);
        popLinearLayout.setBackgroundColor(Color.parseColor("#ffffff"));//设置背景色
        /*<- 弹窗LinearLayout布局*/
        /*PopWindow ->*/
        popupWindow = new PopupWindow(this);
        popupWindow.setContentView(popLinearLayout);
        popupWindow.setFocusable(true); // 设置PopupWindow可获得焦点
        popupWindow.setWidth(_256dp);
        /*<- PopWindow*/
    }

    /*添加下拉刷新*/
    private void initRefresh() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorRefresh);//主题
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (swipeRefreshLayout.getVerticalScrollbarPosition() == 0) {//判断页面是否在最上方
                    AlertDialog.Builder dialog = new AlertDialog.Builder(Class_PersonalCourseTable.this);
                    dialog.setCancelable(true);
                    dialog.setTitle("说明：");
                    dialog.setMessage("* 编辑模式下长按可进行修改\n* 同名课程取首节课的课程信息");
                    dialog.setNegativeButton("刷新", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            freeButton();
                            if (file.exists() && havePermission) {
                                JsonString = getJsonStringFromMemory();
                            } else {
                                JsonString = getLocalJson("coursetable_json.txt");
                            }
                            getCourseArray();
                            setCourseLength();
                            addLongCourse();
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                    dialog.setPositiveButton("编辑模式", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            freeButton();
                            if (file.exists() && havePermission) {
                                JsonString = getJsonStringFromMemory();
                            } else {
                                JsonString = getLocalJson("coursetable_json.txt");
                            }
                            getCourseArray();
                            addButton();
                            saveData();
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                    dialog.show();
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    return;
                }

            }
        });
    }

    /*设置课程长度*/
    private void setCourseLength() {
        for (int i = 0; i < 7; i++)//初始化课表长度
            for (int j = 0; j < 10; j++)
                courseLength[i][j] = 1;
        for (int i = 0; i < 7; i++) {
            for (int j = 8; j >= 0; j--) {
                if (!courseNameArray[i][j].equals("")) {//禁止比较空格
                    if (j >= 0 && j < 3 || j >= 4 && j < 7 || j >= 8 && j < 9) {//禁止跨时段比较
                        if (courseNameArray[i][j].equals(courseNameArray[i][j + 1])) {//连接同名课程
                            courseLength[i][j] = courseLength[i][j + 1] + 1;
                            courseLength[i][j + 1] = courseLength[i][j + 1] - courseLength[i][j + 1];
                            getCourseArray();
                        }
                    }
                }
            }
        }
    }

    /*添加长按钮*/
    private void addLongCourse() {
        LinearLayout.LayoutParams longLayoutParams = null;
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 10; j++) {
                button[i][j] = new Button(this);
                longLayoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, courseLength[i][j] * _58dp + (courseLength[i][j] - 1) * _2dp);
                longLayoutParams.setMargins(_2dp, (int) getResources().getDimension(R.dimen._2dp), 0, 0);//设置边距
                button[i][j].setLayoutParams(longLayoutParams);//设置Button大小
                if(courseLength[i][j]==0){
                    longLayoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, _0dp);
                    longLayoutParams.setMargins(_2dp, 0, 0, 0);//设置边距
                    button[i][j].setLayoutParams(longLayoutParams);
                }
                if (j == 4 || j == 8) {//设置分割线
                    longLayoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, courseLength[i][j] * _58dp + (courseLength[i][j] - 1) * _2dp);
                    longLayoutParams.setMargins(_2dp, (int) getResources().getDimension(R.dimen._12dp), 0, 0);
                    button[i][j].setLayoutParams(longLayoutParams);
                }
                button[i][j].setTextAppearance(getApplicationContext(), R.style.Widget_AppCompat_Button_Borderless);//主题？未生效
                button[i][j].setTag(new ButtonPosition(i, j));//设置坐标
                button[i][j].setTextColor(Color.parseColor("#ffffff"));//设置文字颜色
                button[i][j].setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);//设置文字大小
                button[i][j].setText(courseNameArray[i][j] + "\n" + coursePlaceArray[i][j] + "\n" + courseTeacherArray[i][j]);//设置文字
                setButtonBackgroundColor(i,j);//设置背景
                if (button[i][j].getText().charAt(0) == '\n' || button[i][j].getText().equals("")) {//隐藏空Button
                    button[i][j].setBackgroundColor(Color.parseColor("#00000000"));
                }
                day[i].addView(button[i][j]);
            }
        }
    }

    /*添加按钮背景色*/
    private void setButtonBackgroundColor(int i,int j) {
        if (i == 0) {//向布局中添加Button并设置Button背景
            if (j < 4) {
                button[i][j].setBackground(getDrawable(R.drawable.button0));
            } else if (j >= 4 && j < 8) {
                button[i][j].setBackground(getDrawable(R.drawable.button1));
            } else {
                button[i][j].setBackground(getDrawable(R.drawable.button2));
            }
        } else if (i == 1) {
            if (j < 4) {
                button[i][j].setBackground(getDrawable(R.drawable.button1));
            } else if (j >= 4 && j < 8) {
                button[i][j].setBackground(getDrawable(R.drawable.button2));
            } else {
                button[i][j].setBackground(getDrawable(R.drawable.button3));
            }
        } else if (i == 2) {
            if (j < 4) {
                button[i][j].setBackground(getDrawable(R.drawable.button2));
            } else if (j >= 4 && j < 8) {
                button[i][j].setBackground(getDrawable(R.drawable.button3));
            } else {
                button[i][j].setBackground(getDrawable(R.drawable.button4));
            }
        } else if (i == 3) {
            if (j < 4) {
                button[i][j].setBackground(getDrawable(R.drawable.button3));
            } else if (j >= 4 && j < 8) {
                button[i][j].setBackground(getDrawable(R.drawable.button4));
            } else {
                button[i][j].setBackground(getDrawable(R.drawable.button5));
            }
        } else if (i == 4) {
            if (j < 4) {
                button[i][j].setBackground(getDrawable(R.drawable.button4));
            } else if (j >= 4 && j < 8) {
                button[i][j].setBackground(getDrawable(R.drawable.button5));
            } else {
                button[i][j].setBackground(getDrawable(R.drawable.button6));
            }
        } else if (i == 5) {
            if (j < 4) {
                button[i][j].setBackground(getDrawable(R.drawable.button5));
            } else if (j >= 4 && j < 8) {
                button[i][j].setBackground(getDrawable(R.drawable.button6));
            } else {
                button[i][j].setBackground(getDrawable(R.drawable.button0));
            }
        } else if (i == 6) {
            if (j < 4) {
                button[i][j].setBackground(getDrawable(R.drawable.button6));
            } else if (j >= 4 && j < 8) {
                button[i][j].setBackground(getDrawable(R.drawable.button0));
            } else {
                button[i][j].setBackground(getDrawable(R.drawable.button1));
            }
        }
        /* 随机色
        Drawable[] drawable=new Drawable[7];
        drawable[0]=getDrawable(R.drawable.button0);
        drawable[1]=getDrawable(R.drawable.button1);
        drawable[2]=getDrawable(R.drawable.button2);
        drawable[3]=getDrawable(R.drawable.button3);
        drawable[4]=getDrawable(R.drawable.button4);
        drawable[5]=getDrawable(R.drawable.button5);
        drawable[6]=getDrawable(R.drawable.button6);
        Random random = new Random();
        int r=random.nextInt(6);
        button[i][j].setBackgroundDrawable(drawable[r]);
        太丑了 */
    }

    /*释放Button空间，清空屏幕*/
    private void freeButton() {
        button = null;
        System.gc();
        for (int j = 0; j < 7; j++) {
            day[j].removeAllViews();
        }
        button = new Button[7][10];
    }
}
