package com.example.personalcoursetable;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.personalcoursetable.Gson.Course;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {
    private Button button[][] = new Button[7][10];//Button数组
    private TextView textView[] = new TextView[2];//分割线TextView
    private LinearLayout.LayoutParams layoutParams;//布局参数
    private int buttonTopSpace;//相邻Button间隔
    private LinearLayout day0;//周一LinearLayout
    private LinearLayout day1;
    private LinearLayout day2;
    private LinearLayout day3;
    private LinearLayout day4;
    private LinearLayout day5;
    private LinearLayout day6;//周日LinearLayout
    private int buttonHeight;//按钮高度
    private String JsonString;//课表Json字符串
    private String[][] courseArray = new String[7][10];//课表数据
    private String[][] courseNameArray = new String[7][10];//课程名称
    private int Permission_WRITE_EXTERNAL_STORAGE = 0x001;//读写权限
    private File file;//手机存储文件
    private boolean havePermission = false;//判断权限

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        day0 = (LinearLayout) findViewById(R.id.day0);
        day1 = (LinearLayout) findViewById(R.id.day1);
        day2 = (LinearLayout) findViewById(R.id.day2);
        day3 = (LinearLayout) findViewById(R.id.day3);
        day4 = (LinearLayout) findViewById(R.id.day4);
        day5 = (LinearLayout) findViewById(R.id.day5);
        day6 = (LinearLayout) findViewById(R.id.day6);
        buttonHeight = getResources().getDimensionPixelSize(R.dimen.buttonHeight);//指定Button高度（res/values/dimens/...）
        buttonTopSpace = (int) getResources().getDimension(R.dimen.buttonTopSpace);//相邻Button间隔
        /*权限 ->*/
        if (ContextCompat.checkSelfPermission(MainActivity.this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{WRITE_EXTERNAL_STORAGE}, Permission_WRITE_EXTERNAL_STORAGE);
        } else {
            havePermission = true;
        }/*<- 权限*/
        /*布局 ->*/
        layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, buttonHeight);//布局参数
        layoutParams.setMargins(buttonTopSpace, buttonTopSpace, 0, 0);//设置边距
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
        addButton();//添加Button
    }

    @Override/*按钮点击事件*/
    public void onClick(View view) {
        Button onClickButton = (Button) view;
        ButtonPosition buttonPosition = (ButtonPosition) onClickButton.getTag();
        int X = buttonPosition.x, Y = buttonPosition.y;
        if (!button[X][Y].getText().equals("")) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setTitle(courseNameArray[X][Y]);
            dialog.setMessage(courseArray[X][Y]);
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
        if (requestCode == Permission_WRITE_EXTERNAL_STORAGE) {
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
    private void addButton() {
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 10; j++) {
                LinearLayout.LayoutParams spaceLayoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, buttonHeight);//布局参数
                spaceLayoutParams.setMargins(buttonTopSpace, (int) getResources().getDimension(R.dimen.buttonSpace), 0, 0);//设置边距
                /*Button*/
                button[i][j] = new Button(this);
                button[i][j].setLayoutParams(layoutParams);//设置Button大小
                button[i][j].setId(i * 2 + j * 3);//随意的id
                button[i][j].setTag(new ButtonPosition(i, j));//设置坐标
                button[i][j].setTextColor(Color.parseColor("#ffffff"));//设置文字颜色
                button[i][j].setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);//设置文字大小
                button[i][j].setText(courseNameArray[i][j]);//设置文字
                if (j == 4 || j == 8) {//设置分割线
                    button[i][j].setLayoutParams(spaceLayoutParams);
                }
                button[i][j].setOnClickListener(this);//Button点击事件
                button[i][j].setOnLongClickListener(this);
                if (i == 0) {//向布局中添加Button并设置Button背景
                    button[0][j].setBackground(getResources().getDrawable(R.drawable.button0_background));
                    day0.addView(button[i][j]);
                } else if (i == 1) {
                    button[i][j].setBackground(getResources().getDrawable(R.drawable.button1_background));
                    day1.addView(button[i][j]);
                } else if (i == 2) {
                    button[i][j].setBackground(getResources().getDrawable(R.drawable.button2_background));
                    day2.addView(button[i][j]);
                } else if (i == 3) {
                    button[i][j].setBackground(getResources().getDrawable(R.drawable.button3_background));
                    day3.addView(button[i][j]);
                } else if (i == 4) {
                    button[i][j].setBackground(getResources().getDrawable(R.drawable.button4_background));
                    day4.addView(button[i][j]);
                } else if (i == 5) {
                    button[i][j].setBackground(getResources().getDrawable(R.drawable.button5_background));
                    day5.addView(button[i][j]);
                } else if (i == 6) {
                    button[i][j].setBackground(getResources().getDrawable(R.drawable.button6_background));
                    day6.addView(button[i][j]);
                }
                if (button[i][j].getText().equals("")) {//隐藏无内容Button
                    button[i][j].setBackgroundColor(Color.parseColor("#00000000"));
                }
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
            Toast.makeText(getApplicationContext(), "异常位置：String getLocalJson(String filename)/n异常类型：Exception", Toast.LENGTH_LONG).show();
        }
        return JsonString;
    }

    /*获取课表数据，置入courseArray和courseNameArray*/
    private void getCourseArray() {
        int i;
        Gson gson = new Gson();
        Course course = gson.fromJson(JsonString, Course.class);
        try {
            for (i = 0; i < 10; i++) {
                if (course.getMondayCourse() != null) {
                    if (course.getMondayCourse().get(i) != null) {
                        courseNameArray[0][i] = course.getMondayCourse().get(i).getCourseName();
                        courseArray[0][i] = "时间：" + course.getMondayCourse().get(i).getCourseWeek().toString() + "\n"
                                + "地点：" + course.getMondayCourse().get(i).getCoursePlace() + "\n"
                                + "教师：" + course.getMondayCourse().get(i).getCourseTeacher() + "\n";
                    }
                } else {
                    courseArray[0][i] = "";
                }
            }
            for (i = 0; i < 10; i++) {
                if (course.getTuesdayCourse() != null) {
                    if (course.getTuesdayCourse().get(i) != null) {
                        courseNameArray[1][i] = course.getTuesdayCourse().get(i).getCourseName();
                        courseArray[1][i] = "时间：" + course.getTuesdayCourse().get(i).getCourseWeek().toString() + "\n"
                                + "地点：" + course.getTuesdayCourse().get(i).getCoursePlace() + "\n"
                                + "教师：" + course.getTuesdayCourse().get(i).getCourseTeacher() + "\n";
                    }
                } else {
                    courseArray[1][i] = "";
                }

            }
            for (i = 0; i < 10; i++) {
                if (course.getWednesdayCourse() != null) {
                    if (course.getWednesdayCourse().get(i) != null) {
                        courseNameArray[2][i] = course.getWednesdayCourse().get(i).getCourseName();
                        courseArray[2][i] = "时间：" + course.getWednesdayCourse().get(i).getCourseWeek().toString() + "\n"
                                + "地点：" + course.getWednesdayCourse().get(i).getCoursePlace() + "\n"
                                + "教师：" + course.getWednesdayCourse().get(i).getCourseTeacher() + "\n";
                    }
                } else {
                    courseArray[2][i] = "";
                }
            }
            for (i = 0; i < 10; i++) {
                if (course.getTuesdayCourse() != null) {
                    if (course.getThursdayCourse().get(i) != null) {
                        courseNameArray[3][i] = course.getThursdayCourse().get(i).getCourseName();
                        courseArray[3][i] = "时间：" + course.getThursdayCourse().get(i).getCourseWeek().toString() + "\n"
                                + "地点：" + course.getThursdayCourse().get(i).getCoursePlace() + "\n"
                                + "教师：" + course.getThursdayCourse().get(i).getCourseTeacher() + "\n";
                    }
                } else {
                    courseArray[3][i] = "";
                }
            }
            for (i = 0; i < 10; i++) {
                if (course.getFridayCourse() != null) {
                    if (course.getFridayCourse().get(i) != null) {
                        courseNameArray[4][i] = course.getFridayCourse().get(i).getCourseName();
                        courseArray[4][i] = "时间：" + course.getFridayCourse().get(i).getCourseWeek().toString() + "\n"
                                + "地点：" + course.getFridayCourse().get(i).getCoursePlace() + "\n"
                                + "教师：" + course.getFridayCourse().get(i).getCourseTeacher() + "\n";
                    }
                } else {
                    courseArray[4][i] = "";
                }
            }
            for (i = 0; i < 10; i++) {
                if (course.getSaturdayCourse() != null) {
                    if (course.getSaturdayCourse().get(i) != null) {
                        courseNameArray[5][i] = course.getSaturdayCourse().get(i).getCourseName();
                        courseArray[5][i] = "时间：" + course.getSaturdayCourse().get(i).getCourseWeek().toString() + "\n"
                                + "地点：" + course.getSaturdayCourse().get(i).getCoursePlace() + "\n"
                                + "教师：" + course.getSaturdayCourse().get(i).getCourseTeacher() + "\n";
                    }
                } else {
                    courseArray[5][i] = "";
                }
            }
            for (i = 0; i < 10; i++) {
                if (course.getSundayCourse() != null) {
                    if (course.getSundayCourse().get(i) != null) {
                        courseNameArray[6][i] = course.getSundayCourse().get(i).getCourseName();
                        courseArray[6][i] = "时间：" + course.getSundayCourse().get(i).getCourseWeek().toString() + "\n"
                                + "地点：" + course.getSundayCourse().get(i).getCoursePlace() + "\n"
                                + "教师：" + course.getSundayCourse().get(i).getCourseTeacher() + "\n";
                    }
                } else {
                    courseArray[6][i] = "";
                }
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "异常位置：void getCourseArray()\n异常类型：Exception", Toast.LENGTH_LONG).show();
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
            Toast.makeText(getApplicationContext(), "异常位置：void saveData()\n异常类型：FileNotFoundException", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "异常位置：void saveData()\n异常类型：IOException", Toast.LENGTH_LONG).show();
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
            Toast.makeText(getApplicationContext(), "异常位置：String getJsonStringFromMemory()/n异常类型：读取缓存数据失败", Toast.LENGTH_SHORT).show();
        }
        return "";
    }

    /*修改JSON文件*/
    private void reviseJson(int X, int Y) {
        Intent intent = new Intent(MainActivity.this, CourseChange.class);
        startActivity(intent);
    }

    /*删除JSON文件*/
    private void deleteJson(int X, int Y) {
        courseArray[X][Y] = "";
        courseNameArray[X][Y] = "";
        button[X][Y].setBackgroundColor(Color.parseColor("#00000000"));
        button[X][Y].setText("");
        Gson gson = new Gson();
        Course course = gson.fromJson(JsonString, Course.class);
        setJson(course, X, Y, "", "", "", "");
        JsonString = gson.toJson(course);
        saveData();
    }

    /*修改和删除JSON文件的中间方法*/
    private void setJson(Course course, int X, int Y, String courseName, String coursePlace, String courseTeacher, String courseWeek) {
        try {
            switch (X) {
                case 0:
                    course.getMondayCourse().get(Y).setCourseName(courseName);
                    course.getMondayCourse().get(Y).setCoursePlace(coursePlace);
                    course.getMondayCourse().get(Y).setCourseTeacher(courseTeacher);
                    course.getMondayCourse().get(Y).setCourseWeek(courseWeek);
                    break;
                case 1:
                    course.getTuesdayCourse().get(Y).setCourseName(courseName);
                    course.getTuesdayCourse().get(Y).setCoursePlace(coursePlace);
                    course.getTuesdayCourse().get(Y).setCourseTeacher(courseTeacher);
                    course.getTuesdayCourse().get(Y).setCourseWeek(courseWeek);
                    break;
                case 2:
                    course.getWednesdayCourse().get(Y).setCourseName(courseName);
                    course.getWednesdayCourse().get(Y).setCoursePlace(coursePlace);
                    course.getWednesdayCourse().get(Y).setCourseTeacher(courseTeacher);
                    course.getWednesdayCourse().get(Y).setCourseWeek(courseWeek);
                    break;
                case 3:
                    course.getThursdayCourse().get(Y).setCourseName(courseName);
                    course.getThursdayCourse().get(Y).setCoursePlace(coursePlace);
                    course.getThursdayCourse().get(Y).setCourseTeacher(courseTeacher);
                    course.getThursdayCourse().get(Y).setCourseWeek(courseWeek);
                    break;
                case 4:
                    course.getFridayCourse().get(Y).setCourseName(courseName);
                    course.getFridayCourse().get(Y).setCoursePlace(coursePlace);
                    course.getFridayCourse().get(Y).setCourseTeacher(courseTeacher);
                    course.getFridayCourse().get(Y).setCourseWeek(courseWeek);
                    break;
                case 5:
                    course.getSaturdayCourse().get(Y).setCourseName(courseName);
                    course.getSaturdayCourse().get(Y).setCoursePlace(coursePlace);
                    course.getSaturdayCourse().get(Y).setCourseTeacher(courseTeacher);
                    course.getSaturdayCourse().get(Y).setCourseWeek(courseWeek);
                    break;
                case 6:
                    course.getSundayCourse().get(Y).setCourseName(courseName);
                    course.getSundayCourse().get(Y).setCoursePlace(coursePlace);
                    course.getSundayCourse().get(Y).setCourseTeacher(courseTeacher);
                    course.getSundayCourse().get(Y).setCourseWeek(courseWeek);
                    break;
                default:
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "异常位置：void setJson(...)\n异常类型：引用null", Toast.LENGTH_LONG).show();
        }
    }

    /*将JSON中的null填充*/
    private void addJson() {
        int i;
        Gson gson = new Gson();
        Course course = gson.fromJson(JsonString, Course.class);
        try {
            for (i = 0; i < 10; i++) {
                if (course.getMondayCourse() != null) {
                    if (course.getMondayCourse().get(i) == null) {
                        course.getMondayCourse().set(i, new Course.DayCourse("", "", "", ""));
                    }
                } else {

                }
            }
            for (i = 0; i < 10; i++) {
                if (course.getTuesdayCourse() != null) {
                    if (course.getTuesdayCourse().get(i) == null) {
                        course.getTuesdayCourse().set(i, new Course.DayCourse("", "", "", ""));
                    }
                } else {

                }
            }
            for (i = 0; i < 10; i++) {
                if (course.getWednesdayCourse() != null) {
                    if (course.getWednesdayCourse().get(i) == null) {
                        course.getWednesdayCourse().set(i, new Course.DayCourse("", "", "", ""));
                    }
                } else {

                }
            }
            for (i = 0; i < 10; i++) {
                if (course.getThursdayCourse() != null) {
                    if (course.getThursdayCourse().get(i) == null) {
                        course.getThursdayCourse().set(i, new Course.DayCourse("", "", "", ""));
                    }
                } else {

                }
            }
            for (i = 0; i < 10; i++) {
                if (course.getFridayCourse() != null) {
                    if (course.getFridayCourse().get(i) == null) {
                        course.getFridayCourse().set(i, new Course.DayCourse("", "", "", ""));
                    }
                } else {

                }
            }
            for (i = 0; i < 10; i++) {
                if (course.getSaturdayCourse() != null) {
                    if (course.getSaturdayCourse().get(i) == null) {
                        course.getSaturdayCourse().set(i, new Course.DayCourse("", "", "", ""));
                    }
                } else {

                }
            }
            for (i = 0; i < 10; i++) {
                if (course.getSundayCourse() != null) {
                    if (course.getSundayCourse().get(i) == null) {
                        course.getSundayCourse().set(i, new Course.DayCourse("", "", "", ""));
                    }
                } else {

                }
            }
            JsonString = gson.toJson(course);
            saveData();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "异常位置：void getCourseArray()\n异常类型：Exception", Toast.LENGTH_LONG).show();
        }
    }
}
