package com.deemsysinc.kidsar;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.deemsysinc.kidsar.models.PuzzleModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Size;

public class PuzzleDetail extends AppCompatActivity implements View.OnDragListener, View.OnTouchListener, TextToSpeech.OnInitListener {
    TextView puzzleheader, objectHeader;
    ImageView buttonBack;
    LinearLayout dragview;
    CardView dropview;
    ImageView image1, image2, image3, image4;
    Dialog dialog;
    int[] arraycongrats;
    View puzzledetail;
    MediaPlayer failuresuccessmp = null;
    int answerIndex, otherIndex1, otherIndex2, otherIndex3;
    List<PuzzleModel> puzzleList = new ArrayList<>();
    List<PuzzleModel> templist = new ArrayList<>();
    List<PuzzleModel> templistReload = new ArrayList<>();
    int answerposition;


    Timer mTimer1;
    TimerTask mTt1;
    Handler mTimerHandler = new Handler();

    TextToSpeech Speech;
    String settheVoice = "en-us-x-sfg#male_1-local";
    String objectHeaderString, headertext = "";

    String name;
    BitmapDrawable background;
    int position = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_puzzle_detail);

        //Greeeting pages dynamically change the image
        arraycongrats = new int[]{R.drawable.greet1, R.drawable.greet3, R.drawable.greet4};

        Speech = new TextToSpeech(PuzzleDetail.this, PuzzleDetail.this);
        //Drag and DropView
        dragview = findViewById(R.id.dragview);
        dropview = findViewById(R.id.dropview);
        dropview.setOnDragListener(this);

        //puzzle Detail for Background image, puzzle Header for set Title and object Header for Puzzle Question
        puzzleheader = findViewById(R.id.puzzleheader);
        puzzledetail = findViewById(R.id.puzzledetail);
        objectHeader = findViewById(R.id.objectHeader);

        //Back Navigation
        buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PuzzleDetail.this, PuzzleActivity.class));
            }
        });
        Intent intent = getIntent();
        if (intent != null) {
            name = intent.getExtras().getString("PuzzleHeader");
            int puzzleIndex = intent.getIntExtra("PuzzleIndex", -1);
            switch (puzzleIndex) {
                case 0:
                    puzzledetail.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_puzzle_fruits));
                    position = 2;
                    break;
                case 1:
                    puzzledetail.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_puzzle_animal));
                    position = 1;
                    break;
                default:
                    startActivity(new Intent(PuzzleDetail.this, PuzzleActivity.class));
                    break;
            }
        }
        puzzleheader.setText(name);
        new AsyncTaskClass().execute("");
    }

    private void getPuzzleQuestion() {
        if (puzzleList.size() > 1) {
            answerIndex = getRandomQuestion(puzzleList.size());
            otherIndex1 = -1;
            otherIndex2 = -1;
            otherIndex3 = -1;
            objectHeaderString = puzzleList.get(answerIndex).getName();
            headertext = "FIND " + objectHeaderString;
            objectHeader.setText(headertext);
            answerposition = getRandomQuestion(4);
            Log.d("Index", "answerIndex : " + answerIndex + " answerPosition : " + answerposition);
            LoadPuzzleAnswers(answerIndex);
        }
    }

    private void LoadPuzzleAnswers(int answerIndex) {

        //Remove Answer Index in List
        templist = new ArrayList<>(puzzleList);
        templist.remove(answerIndex);

        templistReload = new ArrayList<>(templist);

        //Remove the Views
        if (dropview.getChildCount() > 0)
            dropview.removeAllViews();

        if (dragview != null && dragview.getChildCount() > 0) {
            dragview.removeAllViews();
        }

        //Reinitialize the Drag Views
        LayoutInflater inflater = LayoutInflater.from(PuzzleDetail.this);
        View child = inflater.inflate(R.layout.dragview_puzzle, dragview, false);
        dragview.addView(child);

        image1 = child.findViewById(R.id.image1);
        image2 = child.findViewById(R.id.image2);
        image3 = child.findViewById(R.id.image3);
        image4 = child.findViewById(R.id.image4);
        image1.setOnTouchListener(PuzzleDetail.this);
        image2.setOnTouchListener(PuzzleDetail.this);
        image3.setOnTouchListener(PuzzleDetail.this);
        image4.setOnTouchListener(PuzzleDetail.this);


        String answerpath = puzzleList.get(answerIndex).getImage();
        switch (answerposition) {
            case 0:
                image1.setImageBitmap(getBitmap(answerpath));
                image1.setTag(puzzleList.get(answerIndex).getName());
                otherPositions(image2, image3, image4);
                break;
            case 1:
                image2.setImageBitmap(getBitmap(answerpath));
                image2.setTag(puzzleList.get(answerIndex).getName());
                otherPositions(image1, image3, image4);
                break;
            case 2:
                image3.setImageBitmap(getBitmap(answerpath));
                image3.setTag(puzzleList.get(answerIndex).getName());
                otherPositions(image1, image2, image4);
                break;
            case 3:
                image4.setImageBitmap(getBitmap(answerpath));
                image4.setTag(puzzleList.get(answerIndex).getName());
                otherPositions(image1, image2, image3);
                break;
        }
    }

    private void otherPositions(ImageView image1, ImageView image2, ImageView image3) {
        for (int i = 0; i < 3; i++) {
            int randpos = getRandomQuestion(templist.size());
            switch (i) {
                case 0:
                    if (otherIndex1 == -1) {
                        otherIndex1 = randpos;
                        image1.setImageBitmap(getBitmap(templist.get(randpos).getImage()));
                        image1.setTag(templist.get(randpos).getName());
                    } else {
                        image1.setImageBitmap(getBitmap(templistReload.get(otherIndex1).getImage()));
                        image1.setTag(templistReload.get(otherIndex1).getName());
                    }
                    break;
                case 1:
                    if (otherIndex2 == -1) {
                        otherIndex2 = randpos;
                        image2.setImageBitmap(getBitmap(templist.get(randpos).getImage()));
                        image2.setTag(templist.get(randpos).getName());
                    } else {
                        image2.setImageBitmap(getBitmap(templistReload.get(otherIndex2).getImage()));
                        image2.setTag(templistReload.get(otherIndex2).getName());
                    }
                    break;
                case 2:
                    if (otherIndex3 == -1) {
                        otherIndex3 = randpos;
                        image3.setImageBitmap(getBitmap(templist.get(randpos).getImage()));
                        image3.setTag(templist.get(randpos).getName());
                    } else {
                        image3.setImageBitmap(getBitmap(templistReload.get(otherIndex3).getImage()));
                        image3.setTag(templistReload.get(otherIndex3).getName());
                    }
                    break;
            }
            templist.remove(randpos);
        }
    }

    private void LoadJsonValuesFA(int position) {
        JSONArray puzzlearray = null;
        try {
            puzzlearray = new JSONArray(loadJSONFromAsset());
            JSONObject levelObject = puzzlearray.getJSONObject(position);
            JSONArray jsonArray = levelObject.getJSONArray("models");
            for (int k = 0; k < jsonArray.length(); k++) {
                JSONObject animalfruitobj = jsonArray.getJSONObject(k);
                puzzleList.add(new PuzzleModel(k, animalfruitobj.getString("modelName"), animalfruitobj.getString("modelImage")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("kids.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }


    private Bitmap getBitmap(String path) {
        Bitmap decoded = null;
        try {
            Bitmap bitmap1 = BitmapFactory.decodeStream(getAssets().open(path));
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap1.compress(Bitmap.CompressFormat.PNG, 100, out);
            decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return decoded;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(PuzzleDetail.this, PuzzleActivity.class));
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        int action = event.getAction();
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                // do nothing
                if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                    return true;
                }
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                break;
            case DragEvent.ACTION_DROP:
                // Dropped, reassign View to ViewGroup
                View view = (View) event.getLocalState();
                if (view.getTag().equals(objectHeaderString)) {
                    mediaPlayerTone(R.raw.kids_cheer, true);
                    //For Removing Views from existing Layout
                    ViewGroup owner = (ViewGroup) view.getParent();
                    owner.removeView(view);
                    view.setVisibility(View.INVISIBLE);
                    CardView container = (CardView) v;
                   /* LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    params.gravity = Gravity.CENTER;*/
                    container.addView(view);
                    view.setVisibility(View.VISIBLE);
                    dropview.clearAnimation();
                    dialog = new Dialog(PuzzleDetail.this);
                    dialog.setContentView(R.layout.dialog_congrats);
                    ViewGroup laycontainer = dialog.findViewById(R.id.shower);
                    KonfettiView konfettiView = dialog.findViewById(R.id.konfettiView);
                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                    float width = displayMetrics.widthPixels;
                    float height = displayMetrics.heightPixels;
                    startTimer(konfettiView, width);
                    dialog.setCancelable(false);
                    dialog.show();
                    LoadPuzzleAnswers(answerIndex);
                    int val = getRandom(arraycongrats);
                    CircleImageView circleimageview = dialog.findViewById(R.id.circleimageview);
                    circleimageview.setImageDrawable(ContextCompat.getDrawable(PuzzleDetail.this, val));
                    ImageView close_puzzle = dialog.findViewById(R.id.close_puzzle);
                    close_puzzle.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mediaPlayerStop();
                            dialog.dismiss();
                            stopTimer();
                        }
                    });
                    TextView playnext = dialog.findViewById(R.id.playnext);
                    playnext.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mediaPlayerStop();
                            dialog.dismiss();
                            stopTimer();
                            getPuzzleQuestion();
                            Speak(headertext);
                        }
                    });
                } else {
                    mediaPlayerTone(R.raw.negative_tone, false);
                    Animation anim = AnimationUtils.loadAnimation(PuzzleDetail.this, R.anim.value_vibrate);
                    dropview.startAnimation(anim);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mediaPlayerStop();
                            dropview.clearAnimation();
                        }
                    }, 500);
                }
                break;
            case DragEvent.ACTION_DRAG_ENDED:
                View view1 = (View) event.getLocalState();
                if (view1 != null)
                    view1.setVisibility(View.VISIBLE);
            default:
                break;
        }
        return true;
    }

    private void mediaPlayerStop() {
        if (failuresuccessmp != null && failuresuccessmp.isPlaying()) {
            failuresuccessmp.stop();
        }
    }

    private void mediaPlayerTone(int appmusic, boolean loop) {
        failuresuccessmp = MediaPlayer.create(this, appmusic);
        failuresuccessmp.setVolume(100, 100);
        failuresuccessmp.setLooping(loop);
        failuresuccessmp.start();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            v.setVisibility(View.INVISIBLE);
            CharSequence cha = (CharSequence) v.getTag();
            ClipData.Item clipitem = new ClipData.Item(cha);
            ClipData dragData = new ClipData(cha, new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, clipitem);
            View.DragShadowBuilder myShadow = new MyDragShadowBuilder(v);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                v.startDragAndDrop(dragData, myShadow, v, 0);
            } else {
                v.startDrag(dragData, myShadow, v, 0);
            }
            return true;
        } else {
            return false;
        }
    }

    private int MyColour(int color) {
        return ContextCompat.getColor(PuzzleDetail.this, color);
    }

    private static class MyDragShadowBuilder extends View.DragShadowBuilder {
        private static View shadow;

        public MyDragShadowBuilder(View v) {

            super(v);

            shadow = v;
        }

        @Override
        public void onProvideShadowMetrics(Point size, Point touch) {
            View v = getView();
            int height = v.getHeight();
            int width = v.getWidth();
            size.set(width, height);
            touch.set((width / 2), (height / 2));
        }

        @Override
        public void onDrawShadow(Canvas canvas) {
            shadow.draw(canvas);
        }
    }

    public static int getRandom(int[] array) {
        int rnd = new Random().nextInt(array.length);
        return array[rnd];
    }

    public static int getRandomQuestion(int size) {
        return new Random().nextInt(size);
    }

    private void stopTimer() {
        if (mTimer1 != null) {
            mTimer1.cancel();
            mTimer1.purge();
        }
    }

    //Used for Repeated confetti
    private void startTimer(final KonfettiView konfettiView, final float width) {
        mTimer1 = new Timer();
        mTt1 = new TimerTask() {
            public void run() {
                mTimerHandler.post(new Runnable() {
                    public void run() {
                        konfettiView.build()
                                .addColors(MyColour(R.color.colorGold1), MyColour(R.color.colorGold2), MyColour(R.color.colorAccent), MyColour(R.color.colorGold3), MyColour(R.color.colorGold4))
                                .setDirection(0, 359.0)
                                .setSpeed(3f, 6f)
                                .setPosition(0, width, -50, -50f)
                                .setFadeOutEnabled(false)
                                .addShapes(nl.dionsegijn.konfetti.models.Shape.CIRCLE, nl.dionsegijn.konfetti.models.Shape.RECT)
                                .addSizes(new Size(12, 5), new Size(8, 5))
                                .setTimeToLive(5000L)
                                .streamFor(50, 5000L);
                    }
                });
            }
        };

        mTimer1.schedule(mTt1, 100L, 5000L);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = Speech.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.d("KidsAR", "This Language is not Supported");
            }
        }
        Speak(headertext);
    }

    private void Speak(String name) {
        Speech.setPitch(1f);
        Speech.setSpeechRate(0.7f);
        Speech.speak(name, TextToSpeech.QUEUE_FLUSH, null, "id1");
        if (Speech.getVoices() != null) {
            for (Voice voice : Speech.getVoices()) {
                if (voice.getName().equals(settheVoice)) {
                    Speech.setVoice(voice);
                }
            }
        }

    }

    public class AsyncTaskClass extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            LoadJsonValuesFA(position);
//            background = new BitmapDrawable(getResources(), getBitmap(backgroundImage));
            /*Intent intent = getIntent();
            if (intent != null) {
                name = intent.getExtras().getString("PuzzleHeader");
                int puzzleIndex = intent.getIntExtra("PuzzleIndex", -1);
                switch (puzzleIndex) {
                    case 0:
                        background = new BitmapDrawable(getResources(), getBitmap("fruits.png"));
                        LoadJsonValuesFA(2);
                        break;
                    case 1:
                        background = new BitmapDrawable(getResources(), getBitmap("animal.jpg"));
                        LoadJsonValuesFA(1);
                        break;
                    default:
                        startActivity(new Intent(PuzzleDetail.this,PuzzleActivity.class));
                        break;
                }
            }*/
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
//            puzzledetail.setBackground(background);
            getPuzzleQuestion();
        }
    }
}
