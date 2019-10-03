package com.example.tappyspaceship01;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Rect;
import android.media.Image;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

public class GameEngine extends SurfaceView implements Runnable {

    // Android debug variables
    final static String TAG="DINO-RAINBOWS";


    // -----------------------------------
    // ## SCREEN & DRAWING SETUP VARIABLES
    // -----------------------------------

    // screen size
    int screenHeight;
    int screenWidth;


    Bitmap ememyImage;
    int enemyXPosition;
    int enemyYPosition;
    Rect enemyHitbox;

    // game state
    boolean gameIsRunning;

    // threading
    Thread gameThread;


    // drawing variables
    SurfaceHolder holder;
    Canvas canvas;
    Paint paintbrush;



    // -----------------------------------
    // ## GAME SPECIFIC VARIABLES
    // -----------------------------------

    // ----------------------------
    // ## SPRITES
    // ----------------------------
    int ballXPosition;      // keep track of ball -x
    int ballYPosition;      // keep track of ball -y

    int racketXPosition;  // top left corner of the racket
    int racketYPosition;  // top left corner of the racket

//    int racket2XPosition;
//    int racket2YPosition;
    // ----------------------------
    // ## GAME STATS - number of lives, score, etc
    // ----------------------------
    int score = 0;
    private Object Picture;

    public GameEngine(Context context, int w, int h) {
        super(context);


        this.holder = this.getHolder();
        this.paintbrush = new Paint();

        this.screenWidth = w;
        this.screenHeight = h;


        this.printScreenInfo();



        this.ememyImage = BitmapFactory.decodeResource(this.getContext().getResources(),
                R.drawable.rainbow32);


        this.enemyXPosition = 1300;
        this.enemyYPosition = 120;
        // 1. create the hitbox
        this.enemyHitbox = new Rect(1300,
                120,
                1300+ememyImage.getWidth(),
                120+ememyImage.getHeight()
        );

        // @TODO: Add your sprites to this section
        // This is optional. Use it to:
        //  - setup or configure your sprites
        //  - set the initial position of your sprites
        this.ballXPosition = this.screenWidth;
        this.ballYPosition = this.screenHeight;


        // Setup the initial position of the racket
        this.racketXPosition = 0;
        this.racketYPosition = 0;

//        this.racket2XPosition = 200;
//       this.racket2YPosition = 200;
        // @TODO: Any other game setup stuff goes here


    }

    // ------------------------------
    // HELPER FUNCTIONS
    // ------------------------------

    // This funciton prints the screen height & width to the screen.
    private void printScreenInfo() {

        Log.d(TAG, "Screen (w, h) = " + this.screenWidth + "," + this.screenHeight);
    }


    // ------------------------------
    // GAME STATE FUNCTIONS (run, stop, start)
    // ------------------------------
    @Override
    public void run() {
        while (gameIsRunning == true) {
            this.updatePositions();
            this.redrawSprites();
            this.setFPS();
        }
    }


    public void pauseGame() {
        gameIsRunning = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            // Error
        }
    }

    public void startGame() {
        gameIsRunning = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    public class Player {

        private Bitmap bitmap;


        private int x;
        private int y;


        private int speed = 0;


        public Player(Context context) {
            x = 75;
            y = 50;
            speed = 1;


            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.dino32);
        }

        //Method to update coordinate of character
        public void update(){
            //updating x coordinate
            x++;
        }

        /*
         * These are getters you can generate it autmaticallyl
         * right click on editor -> generate -> getters
         * */
        public Bitmap getBitmap() {
            return bitmap;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getSpeed() {
            return speed;
        }
    }
    // ------------------------------
    // GAME ENGINE FUNCTIONS
    // - update, draw, setFPS
    // ------------------------------


    String directionBallIsMoving = "right";
    String personTapped="";

    // 1. Tell Android the (x,y) positions of your sprites
    public void updatePositions() {
        // @TODO: Update the position of the sprites

        if (directionBallIsMoving == "right") {
            this.ballXPosition = this.ballXPosition + 25;

            // if ball hits the floor, then change its direciton
            if (this.ballXPosition >= this.screenWidth) {
                //Log.d(TAG, "BALL HIT THE FLOOR / OUT OF BOUNDS");

                // Restart the game
                // Put everything back in their default positions
                // --------------------
                // restart the ball
                this.ballXPosition = this.screenWidth /2 -1000 ;
                this.ballYPosition = this.screenHeight / 2 - 400;

                // restart the racket position
                this.racketXPosition = 0;
                this.racketYPosition = 200;


//              this.racket2XPosition = 0;
//               this.racket2YPosition = 400;
                // restart hte direction
                directionBallIsMoving = "right";

                // clear any previous user actions
                personTapped = "";


            }
        }
        if (directionBallIsMoving == "up") {
            this.ballYPosition = this.ballYPosition - 25;

            // if ball hits ceiling, then change directions
            if (this.ballYPosition <= 0 ) {
                // hit upper wall
                //Log.d(TAG,"BALL HIT CEILING / OUT OF BOUNDS ");
                directionBallIsMoving = "down";
            }
        }

//
//        // calculate the racket's new position
//        if (personTapped.contentEquals("right")){
//            this.racketXPosition = this.racketXPosition + 10;
//        }
//        else if (personTapped.contentEquals("left")){
//            this.racketXPosition = this.racketXPosition - 10;
//        }


        // @TODO: Collision detection code

        // detect when ball hits the racket
        // ---------------------------------

        // 1. if ball hits racket, bounce off racket

        // Check that ball is inside the x and y boundaries
        // of the racket

        // BallY + 50 because we want to make a more precise collision
        // When bottom left corner of ball touches racket, then bounce!
        // (ballY+50) = bottom left
        if (
                (ballYPosition + 50) >= (this.racketYPosition) &&
                        ballXPosition >= this.racketXPosition &&
                        ballXPosition <= this.racketXPosition + 400
        ) {

            // ball is touching racket
            Log.d(TAG, "Ball IS TOUCHING RACKET!");
            directionBallIsMoving = "up";

            // increase the game score!
            this.score = this.score + 50;
        }


        // 2. if ball misses racket, then keep going down

        // 3. if ball falls off bottom of screen, restart the ball in middle
    }

    // 2. Tell Android to DRAW the sprites at their positions
    public void redrawSprites() {
        if (this.holder.getSurface().isValid()) {
            this.canvas = this.holder.lockCanvas();

            //----------------
            // Put all your drawing code in this section

            // configure the drawing tools
            this.canvas.drawColor(Color.argb(255,255,0,255));
            paintbrush.setColor(Color.WHITE);

            //@TODO: Draw the sprites (rectangle, circle, etc)

            // 1. Draw the ball
            this.canvas.drawRect(
                    ballXPosition,
                    ballYPosition,
                    ballXPosition + 50,
                    ballYPosition + 50,
                    paintbrush);


           // Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(),R.drawable.dino32);
         //   canvas.drawBitmap(bitmap, null, mRedPaddleRect, mPaint);

this.canvas.drawLine(20,30,50,50,paintbrush);

            // draw player



            // MAKE ENEMY MOVE
            // - enemy moves left forever
            // - when enemy touches LEFT wall, respawn on RIGHT SIDE
            this.enemyXPosition = this.enemyXPosition - 25;

            // MOVE THE HITBOX (recalcluate the position of the hitbox)
            this.enemyHitbox.left  = this.enemyXPosition;
            this.enemyHitbox.top = this.enemyYPosition;
            this.enemyHitbox.right  = this.enemyXPosition + this.ememyImage.getWidth();
            this.enemyHitbox.bottom = this.enemyYPosition + this.ememyImage.getHeight();

            if (this.enemyXPosition <= 0) {
                // restart the enemy in the starting position
                this.enemyXPosition = 1300;
                this.enemyYPosition = 120;

                // restart the hitbox in the starting position
                // Anytime you move the enemy, you also need to move the hitbox
                this.enemyHitbox.left  = this.enemyXPosition;
                this.enemyHitbox.top = this.enemyYPosition;
                this.enemyHitbox.right  = this.enemyXPosition + this.ememyImage.getWidth();
                this.enemyHitbox.bottom = this.enemyYPosition + this.ememyImage.getHeight();

            }



            // 2. Draw the racket

            paintbrush.setColor(Color.BLACK);
            this.canvas.drawRect(this.racketXPosition,
                    this.racketYPosition,
                    this.racketXPosition + 1400,     // 400 is width of racket
                    this.racketYPosition + 50,    // 50 is height of racket
                    paintbrush);
            paintbrush.setColor(Color.WHITE);



//            paintbrush.setColor(Color.RED);
//            this.canvas.drawRect(this.racket2XPosition,
//                    this.racketYPosition,
//                    this.racket2XPosition + 1400,     // 400 is width of racket
//                    this.racket2YPosition + 150,    // 50 is height of racket
//                    paintbrush);
//            paintbrush.setColor(Color.WHITE);
//            // this.canvas.drawRect(left, top, right, bottom, paintbrush);



            //@TODO: Draw game statistics (lives, score, etc)
            paintbrush.setTextSize(60);
            canvas.drawText("Score: " + this.score, 20, 100, paintbrush);
            canvas.drawText("Lives: " + this.score, 40, 200, paintbrush);
            canvas.drawBitmap(ememyImage, enemyXPosition, enemyYPosition, paintbrush);
            // 2. draw the enemy's hitbox

            //----------------
            this.holder.unlockCanvasAndPost(canvas);
        }
    }



    // Sets the frame rate of the game
    public void setFPS() {
        try {
            gameThread.sleep(50);
        }
        catch (Exception e) {

        }
    }

    // ------------------------------
    // USER INPUT FUNCTIONS
    // ------------------------------

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int userAction = event.getActionMasked();
        //@TODO: What should happen when person touches the screen?
        // ---------------------------------------------------------
        // Get position of the tap
        // Compare position of tap to the middle of the screen
        // If tap is on left, move racket Position to left
        // If tap is on right, move racket position to right

        if (userAction == MotionEvent.ACTION_DOWN) {
            // user pushed down on screen

            // 1. Get position of tap
            float fingerXPosition = event.getX();
            float fingerYPosition = event.getY();
            Log.d(TAG, "Person's pressed: "
                    + fingerXPosition + ","
                    + fingerYPosition);


            // 2. Compare position of tap to middle of screen
            int middleOfScreen = this.screenWidth / 2;
            if (fingerXPosition <= middleOfScreen) {
                // 3. If tap is on left, racket should go left
                personTapped = "left";
            }
            else if (fingerXPosition > middleOfScreen) {
                // 4. If tap is on right, racket should go right
                personTapped = "right";
            }
        }
        else if (userAction == MotionEvent.ACTION_UP) {
            // user lifted their finger
        }
        return true;
    }
}