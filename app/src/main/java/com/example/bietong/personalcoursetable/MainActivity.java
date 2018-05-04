package com.example.bietong.personalcoursetable;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {
    private String courseJson;

    private CourseView courseViewArray[][] = new CourseView[7][10];
    private String[][] courseNameArray = new String[7][10];
    private String[][] courseWeekArray = new String[7][10];
    private String[][] coursePlaceArray = new String[7][10];
    private String[][] courseTeacherArray = new String[7][10];
    private int[][] courseHeightArray = new int[7][10];

    private SharedPreferences sharedPreferences;
    private LinearLayout.LayoutParams courseViewParams;
    private LinearLayout[] oneDayLayout = new LinearLayout[7];
    private LinearLayout popLayout;
    private int courseViewWidth;
    private int _2dp;
    private int _58dp;
    private boolean isShowExitButton = false;

    private Button popupButton;
    private PopupWindow popupWindow;
    private SwipeRefreshLayout swipeRefreshLayout;
    private AlertDialog.Builder dialog;
    private FloatingActionButton exitEditButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        loadCourseJsonFromSDCard();
        if (courseJson == null) {
//            TODO:这里获取数据
//            getCourseJsonFromNetworking();
//            return;
            getCourseJsonFromProject();
        }
        setCourseData(courseJson,false);
    }

    public void setCourseData(String courseJson,boolean isRemoveAllView) {
        this.courseJson = courseJson;
        if(isRemoveAllView) {
            removeAllCourseView();
        }
        setCourseJsonToArray();
        setCourseViewHeight();
        addCourseView();
    }

    private void initView() {
        sharedPreferences = getSharedPreferences("PersonalCourseTable", Context.MODE_PRIVATE);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        exitEditButton = findViewById(R.id.fButton);
        int dayId[] = new int[]{R.id.day0, R.id.day1, R.id.day2, R.id.day3, R.id.day4, R.id.day5, R.id.day6};
        for (int i = 0; i < 7; i++) {
            oneDayLayout[i] = findViewById(dayId[i]);
        }
        _2dp = (int) getResources().getDimension(R.dimen._2dp);
        _58dp = (int) getResources().getDimension(R.dimen._58dp);
        courseViewParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, _58dp);
        courseViewParams.setMargins(_2dp, _2dp, 0, 0);
        courseViewWidth = (int) (getWindowManager().getDefaultDisplay().getWidth() / 7.5 - _2dp);
        final ScrollChangeListenerScrollView scrollView = findViewById(R.id.scrollView);
        initPopupWindow();
        initRefreshView();
        scrollView.setScrollChangeListener(new ScrollChangeListenerScrollView.ScrollViewListener() {
            @Override
            public void onScrollChange(ScrollChangeListenerScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                int scrollLength = scrollView.getChildAt(0).getHeight() - v.getHeight();
                if (scrollY > scrollLength / 4 * 3) {
                    exitEditButton.hide();
                } else {
                    if (isShowExitButton) {
                        exitEditButton.show();
                    }
                }
            }
        });
        exitEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAllCourseView();
                setCourseJsonToArray();
                setCourseViewHeight();
                addCourseView();
                isShowExitButton = false;
                exitEditButton.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onClick(View view) {
        try {
            CourseView course = (CourseView) view;
            int X = course.X, Y = course.Y;
            if (!courseNameArray[X][Y].equals("")) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle(courseNameArray[X][Y]);
                dialog.setMessage("课程时间：" + courseWeekArray[X][Y] +
                        "\n上课地点：" + coursePlaceArray[X][Y] +
                        "\n任课教师：" + courseTeacherArray[X][Y]);
                dialog.setCancelable(true);
                dialog.show();
            }
        } catch (Exception e) {
            return;
        }
    }

    @Override
    public boolean onLongClick(View view) {
        CourseView courseViewButtion = (CourseView) view;
        int X = courseViewButtion.X, Y = courseViewButtion.Y;
        showPopupMenu(courseViewArray[X][Y], X, Y);
        return true;
    }

    private void addEditableCourse() {
        try {
            LinearLayout.LayoutParams courseSpaceParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, _58dp);
            courseSpaceParams.setMargins(_2dp, (int) getResources().getDimension(R.dimen._12dp), 0, 0);
            CourseView.isSignleColor = true;
            for (int i = 0; i < 7; i++) {
                for (int j = 0; j < 10; j++) {
                    courseViewArray[i][j] = new CourseView(this, courseViewWidth, _58dp, courseNameArray[i][j]);
                    courseViewArray[i][j].setLayoutParams(courseViewParams);
                    courseViewArray[i][j].setPosition(i, j);
                    if (j == 4 || j == 8) {//设置分割线
                        courseViewArray[i][j].setLayoutParams(courseSpaceParams);
                    }
                    courseViewArray[i][j].setOnClickListener(this);
                    courseViewArray[i][j].setOnLongClickListener(this);
                    if (courseNameArray[i][j].equals("")) {//隐藏无内容Button
                        courseViewArray[i][j].setNull();
                    }
                    oneDayLayout[i].addView(courseViewArray[i][j]);
                }
            }
            CourseView.isSignleColor = false;
        } catch (Exception e) {
            exception("数据异常");
        }
    }

    private void showPopupMenu(View view, final int X, final int Y) {
        PopupMenu popupMenu = new PopupMenu(this, view);//View当前PopupMenu显示的相对View的位置
        popupMenu.getMenuInflater().inflate(R.menu.course, popupMenu.getMenu());//menu布局
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
        popupMenu.show();
    }

    private void getCourseJsonFromProject() {
        String jsonString = "";
        try {
            InputStream is = this.getAssets().open("coursetable_json.txt");
            InputStreamReader reader = new InputStreamReader(is, "GBK");
            BufferedReader bufferedReader = new BufferedReader(reader);
            StringBuffer buffer = new StringBuffer("");
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
                buffer.append("\n");
            }
            jsonString = buffer.toString();
            if (jsonString != null) {
                courseJson = jsonString;
            }
        } catch (Exception e) {
            exception("本地数据异常");
        }
    }

    private void setCourseJsonToArray() {
        Gson gson = new Gson();
        CourseGson courseGson = gson.fromJson(courseJson, CourseGson.class);
        try {
            List<List<CourseGson.DayCourse>> dayCourseList = courseGson.getWeekCourseList();
            for (int i = 0; i < 7; i++) {
                for (int j = 0; j < 10; j++) {
                    if (dayCourseList.get(i) != null) {
                        courseNameArray[i][j] = dayCourseList.get(i).get(j).getCourseName();
                        courseWeekArray[i][j] = dayCourseList.get(i).get(j).getCourseWeek();
                        coursePlaceArray[i][j] = dayCourseList.get(i).get(j).getCoursePlace();
                        courseTeacherArray[i][j] = dayCourseList.get(i).get(j).getCourseTeacher();
                    }
                }
            }
        } catch (Exception e) {
            exception("课程数据异常,请刷新重试");
        }
    }

    private EditText courseNameEditText;
    private EditText courseWeekEditText;
    private EditText coursePlaceEditText;
    private EditText courseTeacherEditText;

    private void reviseJson(final int X, final int Y) {
        final Gson gson = new Gson();
        final CourseGson courseGson = gson.fromJson(courseJson, CourseGson.class);
        courseNameEditText.setText(courseNameArray[X][Y]);
        courseWeekEditText.setText(courseWeekArray[X][Y]);
        coursePlaceEditText.setText(coursePlaceArray[X][Y]);
        courseTeacherEditText.setText(courseTeacherArray[X][Y]);
        popupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setJson(courseGson, X, Y, courseNameEditText.getText().toString(), courseWeekEditText.getText().toString(), coursePlaceEditText.getText().toString(), courseTeacherEditText.getText().toString());
                courseJson = gson.toJson(courseGson);
                saveCourseJsonToSDCard();
                setCourseJsonToArray();
                removeAllCourseView();
                addEditableCourse();
                popupWindow.dismiss();
            }
        });
        popupWindow.showAtLocation(findViewById(R.id.scrollView), Gravity.CENTER, 0, 0);
    }

    private void deleteJson(int X, int Y) {
        courseNameArray[X][Y] = "";
        courseWeekArray[X][Y] = "";
        coursePlaceArray[X][Y] = "";
        courseTeacherArray[X][Y] = "";
        courseViewArray[X][Y].setNull();
        Gson gson = new Gson();
        CourseGson courseGson = gson.fromJson(courseJson, CourseGson.class);
        setJson(courseGson, X, Y, "", "", "", "");
        courseJson = gson.toJson(courseGson);
        saveCourseJsonToSDCard();
    }

    /* 修改和删除JSON文件的中间方法 */
    private void setJson(CourseGson courseGson, int X, int Y, String courseName, String courseWeek, String coursePlace, String courseTeacher) {
        try {
            CourseGson.DayCourse dayCourse=courseGson.getWeekCourseList().get(X).get(Y);
            dayCourse.setCourseName(courseName);
            dayCourse.setCoursePlace(coursePlace);
            dayCourse.setCourseTeacher(courseTeacher);
            dayCourse.setCourseWeek(courseWeek);
        } catch (Exception e) {
            exception("修改失败");
        }
    }

    private void initPopupWindow() {
        int _256dp = (int) getResources().getDimension(R.dimen._256dp);
        courseNameEditText = new EditText(this);
        courseNameEditText.setWidth(_256dp);
        courseNameEditText.setHint("课程名称：");
        courseWeekEditText = new EditText(this);
        courseWeekEditText.setWidth(_256dp);
        courseWeekEditText.setHint("课程时间：");
        coursePlaceEditText = new EditText(this);
        coursePlaceEditText.setWidth(_256dp);
        coursePlaceEditText.setHint("上课地点：");
        courseTeacherEditText = new EditText(this);
        courseTeacherEditText.setWidth(_256dp);
        courseTeacherEditText.setHint("任课教师：");
        popupButton = new Button(this);
        popupButton.setBackgroundColor(Color.parseColor("#676767"));
        popupButton.setWidth(_256dp);
        popupButton.setText("保  存");
        popupButton.setTextColor(Color.parseColor("#ffffff"));
        popupButton.setTextSize(getResources().getDimension(R.dimen._8dp));
        popLayout = new LinearLayout(this);
        popLayout.setOrientation(LinearLayout.VERTICAL);
        popLayout.addView(courseNameEditText);
        popLayout.addView(courseWeekEditText);
        popLayout.addView(coursePlaceEditText);
        popLayout.addView(courseTeacherEditText);
        popLayout.addView(popupButton);
        popLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        popupWindow = new PopupWindow(this);
        popupWindow.setContentView(popLayout);
        popupWindow.setFocusable(true); // 设置PopupWindow可获得焦点
        popupWindow.setWidth(_256dp);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void initRefreshView() {
        swipeRefreshLayout.setColorSchemeResources(R.color.colorRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onRefresh() {
                if (swipeRefreshLayout.getVerticalScrollbarPosition() == 0) {//判断页面是否在最上方
                    dialog = new AlertDialog.Builder(MainActivity.this);
                    dialog.setCancelable(true);
                    dialog.setTitle("说明：");
                    dialog.setMessage("* 初始化会重置编辑数据\n* 编辑模式下长按可进行修改\n* 同名课程取首节课的课程信息");
                    dialog.setNegativeButton("初始化", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            removeAllCourseView();
                            getCourseJsonFromNetworking();
                            swipeRefreshLayout.setRefreshing(false);
                            isShowExitButton = false;
                        }
                    });
                    dialog.setPositiveButton("编辑模式", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            removeAllCourseView();
                            addEditableCourse();
                            swipeRefreshLayout.setRefreshing(false);
                            setExitEditButton();
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

    private void setExitEditButton() {
        isShowExitButton = true;
        exitEditButton.setVisibility(View.VISIBLE);
    }

    private void setCourseViewHeight() {
        try {
            for (int i = 0; i < 7; i++)
                for (int j = 0; j < 10; j++)
                    courseHeightArray[i][j] = 1;
            for (int i = 0; i < 7; i++) {
                for (int j = 8; j >= 0; j--) {
                    if (!courseNameArray[i][j].equals("")) {
                        if (j >= 0 && j < 3 || j >= 4 && j < 7 || j >= 8 && j < 9) {//禁止跨时段比较
                            if (courseNameArray[i][j].equals(courseNameArray[i][j + 1])) {//连接同名课程
                                courseHeightArray[i][j] = courseHeightArray[i][j + 1] + 1;
                                courseHeightArray[i][j + 1] = courseHeightArray[i][j + 1] - courseHeightArray[i][j + 1];
                                setCourseJsonToArray();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            exception("课程数据异常，请刷新重试");
        }
    }

    public void addCourseView() {
        LinearLayout.LayoutParams courseViewParams = null;
        int courseHeight;
        try {
            for (int i = 0; i < 7; i++) {
                for (int j = 0; j < 10; j++) {
                    courseHeight = courseHeightArray[i][j] * _58dp + (courseHeightArray[i][j] - 1) * _2dp;
                    courseViewArray[i][j] = new CourseView(this, courseViewWidth, courseHeight,
                            courseNameArray[i][j] + "\n" + coursePlaceArray[i][j] + "\n" + courseTeacherArray[i][j]);
                    courseViewArray[i][j].setPosition(i, j);
                    courseViewParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, courseHeightArray[i][j] * _58dp + (courseHeightArray[i][j] - 1) * _2dp);
                    courseViewParams.setMargins(_2dp, (int) getResources().getDimension(R.dimen._2dp), 0, 0);
                    courseViewArray[i][j].setLayoutParams(courseViewParams);
                    if (courseHeightArray[i][j] == 0) {
                        courseViewParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen._0dp));
                        courseViewParams.setMargins(_2dp, 0, 0, 0);
                        courseViewArray[i][j].setLayoutParams(courseViewParams);
                    }
                    if (j == 4 || j == 8) {//设置分割线
                        courseViewParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, courseHeightArray[i][j] * _58dp + (courseHeightArray[i][j] - 1) * _2dp);
                        courseViewParams.setMargins(_2dp, (int) getResources().getDimension(R.dimen._12dp), 0, 0);
                        courseViewArray[i][j].setLayoutParams(courseViewParams);
                    }
                    if (courseNameArray[i][j].equals("")) {
                        courseViewArray[i][j].setNull();
                    }
                    oneDayLayout[i].addView(courseViewArray[i][j]);
                }
            }
        } catch (Exception e) {
            exception(null);
        }
    }

    private void removeAllCourseView() {
        courseViewArray = null;
        System.gc();
        for (int j = 0; j < 7; j++) {
            oneDayLayout[j].removeAllViews();
        }
        courseViewArray = new CourseView[7][10];
    }

    private void exception(String text){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("PersonalCourseTable");
        editor.commit();
        getCourseJsonFromProject();
        saveCourseJsonToSDCard();
        setCourseJsonToArray();
        setCourseViewHeight();
        if (text != null) {
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
        }
    }

    private void getCourseJsonFromNetworking() {

    }

    private void saveCourseJsonToSDCard() {
        sharedPreferences.edit().putString("PersonalCourseTable", courseJson).commit();
    }

    private void loadCourseJsonFromSDCard() {
        courseJson = sharedPreferences.getString("PersonalCourseTable", null);
    }
}
