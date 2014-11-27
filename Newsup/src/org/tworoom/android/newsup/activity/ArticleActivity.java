package org.tworoom.android.newsup.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ViewFlipper;

import org.tworoom.android.newsup.R;

import org.tworoom.android.newsup.activity.transmission.structure.TransmissionArticle;
import org.tworoom.android.newsup.background.service.LockScreenService;
import org.tworoom.android.newsup.setting.RbPreference;
import org.tworoom.android.newsup.view.ArticleDetailManager;
import org.tworoom.android.newsup.view.ArticleFlipViewManager;
import org.tworoom.android.newsup.view.ArticleListManager;
import org.tworoom.android.newsup.view.structure.LayoutInfo;

import java.util.ArrayList;

public class ArticleActivity extends Activity implements OnTouchListener {

    private static final int NONE_TAB = 0;
    private static final int DOUBLE_TAB = 2;
    private static final int SWIPE_MIN_DISTANCE = 100;
    private static final int CLICK_MAX_DURATION = 600;

    // MainActivity를 kill 하기위함
    private static Activity mainActivity;

    // 좌표
    private float xAtDown, xAtUp, yAtDown, yAtUp;

    // Double Tab 확인을 위한 변수
    int clickCount;
    long clickStartTime;

    ArticleListManager articleListManager;
    ArticleDetailManager articleDetailManager;
    ArticleFlipViewManager flipperManager;

    private int currentArticleId;


    private boolean isAnimationning;

    private Toast toast;

    private long backKeyPressedTime = 0;

    public static ArticleActivity getInstance() {
        return (ArticleActivity) mainActivity;
    }

    @Override
    public void onDestroy() {
        articleListManager.setZeroScore();
        super.onDestroy();
    }

    public void showGuide() {
        toast = Toast.makeText(getApplicationContext(),
                "\'뒤로\'버튼을 한번 더 누르시거나 좌로 한번더 스와이프 하시면 앱이 종료됩니다.", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            finish();
            toast.cancel();
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        init();

        /***
         * 1. LockScreen Activity에서 MainActivity intent 했을 때 : article detail
         * show 2. App을 바로 실행 했을 때 : article list show
         */
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("startCategory")) {
                articleListManager.insertArticleList(extras.getInt("startCategory"));
                articleListManager.display(articleListManager.getChildChount() - 1);
                return;
            } else if (extras.containsKey("category")) {
                articleListManager.setCategory(extras.getInt("category"));
            } else {
                int articleId = extras.getInt("articleId");
                ArrayList<TransmissionArticle> transferredArticleList = (ArrayList<TransmissionArticle>) getIntent().getSerializableExtra("articleList");
                articleListManager.setAnimation(R.anim.second_left_right_in, R.anim.first_left_right_out);
                articleListManager.insertArticleList(transferredArticleList);
                articleListManager.setCurrentChildIndex(extras.getInt("setCurrentChildIndex"));
                moveArticleDetail(articleId);
                return;
            }
        }
        articleListManager.insertArticleList();
        articleListManager.display(articleListManager.getChildChount() - 1);
    }

    private void init() {

        EditText myEditText = new EditText(this);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(myEditText.getWindowToken(), 0);


        mainActivity = ArticleActivity.this;

        RbPreference pref = new RbPreference(ArticleActivity.this);
        if (pref.getValue(RbPreference.IS_LOCK_SCREEN, false)) {
            Intent intent = new Intent(ArticleActivity.this, LockScreenService.class);
            startService(intent);
        }

        ViewFlipper articleListFlipper = (ViewFlipper) findViewById(R.id.articleListFlipper);
        ViewFlipper articleDetailFlipper = (ViewFlipper) findViewById(R.id.articleDetailFlipper);

        articleListFlipper.setOnTouchListener(this);
        articleDetailFlipper.setOnTouchListener(this);

        articleListManager = new ArticleListManager(this, articleListFlipper, R.layout.view_article_list, 1, R.layout.view_article_network_error);
        ArticleDetailManager.setArticleDetailManager(this, articleDetailFlipper, 0);
        articleDetailManager = ArticleDetailManager.getInstance();

        flipperManager = articleListManager;

        LayoutInfo layoutInfo = LayoutInfo.getInstance();
        layoutInfo.calLayoutInfo(this);
    }

    public void changeCategory(int category) {
        if (flipperManager == articleDetailManager) {
            articleDetailManager.outArticleDetail();
            articleDetailManager.removeAllFlipperItem();
            flipperManager = articleListManager;
            flipperManager.setAnimation(R.anim.in, R.anim.out);
            backEvent();
        } else if (category != articleListManager.getCategory()) {
            articleListManager.setZeroScore();
            articleListManager.setCategory(category);
            articleListManager.removeAllFlipperItem();
            articleListManager.insertArticleList();
            flipperManager.setAnimation(R.anim.in, R.anim.out);
            articleListManager.display(articleListManager.getChildChount() - 1);
        }

    }

    public ArticleListManager getArticleListManager() {
        return articleListManager;
    }

    public void changeTextSize() {
        if (flipperManager == articleDetailManager) {
            articleDetailManager.changeTextSize(currentArticleId);
            ;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (flipperManager == articleDetailManager) {
                    articleDetailManager.setAnimation(R.anim.second_left_right_in, R.anim.first_left_right_out);
                    articleListManager.setAnimation(R.anim.second_left_right_in, R.anim.first_left_right_out);
                    backEvent();
                    return true;
                } else {
                    onBackPressed();
                    return true;
                }
        }
        return super.onKeyDown(keyCode, event);
    }

    // detail -> List
    // menu -> detail, list
    private boolean backEvent() {
        articleListManager.getFlipper().getInAnimation().setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
                Log.e("start", "backEvent start");
                isAnimationning = true;
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                Log.e("end", "backEvent end");
                isAnimationning = false;
                articleListManager.getFlipper().getInAnimation().setAnimationListener(null);
            }
        });

        flipperManager = articleListManager;
        articleDetailManager.outArticleDetail();
        articleListManager.outArticleDetail();
        return true;
    }

    // list -> detail
    private boolean moveArticleDetail(int articleId) {
        articleListManager.getFlipper().getInAnimation().setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
                Log.e("start", "moveArticleDetail start");
                isAnimationning = true;
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                Log.e("end", "moveArticleDetail end");
                isAnimationning = false;
                articleListManager.getFlipper().getInAnimation().setAnimationListener(null);
            }
        });
        this.currentArticleId = articleId;
        flipperManager = articleDetailManager;
        articleListManager.inArticleDetail(articleId);
        articleDetailManager.inArticleDetail(articleId);
        return true;
    }

    // detail, list -> menu
    private boolean moveMenuPage() {
        Intent intent = new Intent(ArticleActivity.this, MenuActivity.class);
        startActivity(intent);
        this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        return false;
    }

    private void initClickCount() {
        clickCount = 0;
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                yAtDown = event.getY();
                xAtDown = event.getX();
                if (clickCount == NONE_TAB) {
                    clickStartTime = System.currentTimeMillis();
                }
                clickCount++;
                break;
            case MotionEvent.ACTION_MOVE:
                /***
                 * TODO : 땡겨지는 에니메이션 주기
                 */
                break;
            case MotionEvent.ACTION_UP:
                yAtUp = event.getY();
                xAtUp = event.getX();

                if (!isAnimationning) {
                    // up
                    if (yAtDown - yAtUp > SWIPE_MIN_DISTANCE) {
                        initClickCount();
                        flipperManager.setAnimation(R.anim.second_up_down_in, R.anim.first_up_down_out);
                        return flipperManager.upDownSwipe(-1);
                        // down
                    } else if (yAtUp - yAtDown > SWIPE_MIN_DISTANCE) {
                        initClickCount();
                        flipperManager.setAnimation(R.anim.first_up_down_in, R.anim.second_up_down_out);
                        return flipperManager.upDownSwipe(1);
                        // left
                    } else if (xAtUp - xAtDown > SWIPE_MIN_DISTANCE) {
                        initClickCount();
                        if (flipperManager != articleDetailManager) {
                            onBackPressed();
                            return false;
                        }
                        articleDetailManager.setAnimation(R.anim.second_left_right_in, R.anim.first_left_right_out);
                        articleListManager.setAnimation(R.anim.second_left_right_in, R.anim.first_left_right_out);
                        return backEvent();
                        // right
                    } else if (xAtDown - xAtUp > SWIPE_MIN_DISTANCE) {
                        initClickCount();
                        if (flipperManager == articleDetailManager || flipperManager.isErrorView()) {
                            return false;
                        }
                        articleListManager.setAnimation(R.anim.first_left_right_in, R.anim.second_up_down_out);
                        return moveArticleDetail(articleListManager.getCurrentViewId());
                    }

                    switch (v.getId()) {
                        case R.id.itemList:
                            return false;
                        case R.id.youtube_1:
                            articleDetailManager.showYoutube(0);
                            return false;
                        case R.id.youtube_2:
                            articleDetailManager.showYoutube(1);
                            return false;
                    }

                    if (clickCount == DOUBLE_TAB) {
                        long time = System.currentTimeMillis() - clickStartTime;
                        if (time <= CLICK_MAX_DURATION) {
                            initClickCount();
                            flipperManager.setAnimation(R.anim.fade_in, R.anim.fade_out);
                            return moveMenuPage();
                        }
                        initClickCount();
                    }
                }
        }

        return true;
    }


    public void changeIsAnimationningFlag() {
        isAnimationning = false;
    }

    public void runOutArticle() {
        articleListManager.runOutArticle();
    }
}
