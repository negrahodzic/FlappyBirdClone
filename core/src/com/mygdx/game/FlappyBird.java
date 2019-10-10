package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture topTube;
	Texture bottomTube;
	Texture gameover;
//	ShapeRenderer shapeRenderer;
	Circle birdCircle;
	BitmapFont font;
	BitmapFont info;

	Texture[] birds;
	int flapState = 0;

	float birdY = 0;
	float velocity = 0;
	float gravity = 2;
	int gameState = 0;

	float gap = 400;
	float maxTubeOffset;
	Random randomGenerator = new Random();

	float tubeVelocity = 6;
	int numberOfTubes = 4;
	float[] tubeX = new float[numberOfTubes];
	float[] tubeOffset = new float[numberOfTubes];
	float distanceBetweenTubes;

	Rectangle[] topTubeRectangles;
	Rectangle[] bottomTubeRectangles;

	int score = 0;
	int scoringTube = 0;


	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
//		shapeRenderer = new ShapeRenderer();
		birdCircle = new Circle();
		birds = new Texture[2];
		birds[0] = new Texture("bird.png");
		birds[1] = new Texture("bird2.png");
		topTube = new Texture("toptube.png");
		bottomTube = new Texture("bottomtube.png");
		maxTubeOffset = Gdx.graphics.getHeight()/2 - gap/2 - 100;
		distanceBetweenTubes = Gdx.graphics.getWidth()/2;
		topTubeRectangles = new Rectangle[numberOfTubes];
		bottomTubeRectangles = new Rectangle[numberOfTubes];
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().scale(10);
		info = new BitmapFont();
		info.setColor(Color.WHITE);
		info.getData().scale(5);
		gameover = new Texture("gameover.png");
		birdY = Gdx.graphics.getHeight()/2 - birds[0].getHeight()/2;
		startGame();
	}

	public void startGame(){
		birdY = Gdx.graphics.getHeight()/2 - birds[0].getHeight()/2;
            for (int i = 0; i < numberOfTubes; i++) {
                tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() / 2 - gap / 2);
                tubeX[i] = Gdx.graphics.getWidth() / 2 - topTube.getWidth() / 2 + Gdx.graphics.getWidth() * 3 / 4 + i * distanceBetweenTubes;

                topTubeRectangles[i] = new Rectangle();
                bottomTubeRectangles[i] = new Rectangle();
            }

	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if (gameState==3) {
			info.draw(batch, "Tap anywhere", Gdx.graphics.getWidth()/2-250,Gdx.graphics.getHeight()-150);
			info.draw(batch, "to start!", Gdx.graphics.getWidth()/2-150 ,Gdx.graphics.getHeight()-250);
			if (Gdx.input.justTouched()) gameState = 1;
		}

		if (gameState==1) {

			if (tubeX[scoringTube] < Gdx.graphics.getWidth()/2 - topTube.getWidth()){
				score++;
				if (scoringTube < numberOfTubes - 1) scoringTube++;
				else scoringTube = 0;
			}

            if (Gdx.input.justTouched()) {
                velocity = -28;
            }

			for (int i = 0; i < numberOfTubes; i++) {

				if (tubeX[i] < -topTube.getWidth()) {
					tubeX[i] += numberOfTubes*distanceBetweenTubes;
					tubeOffset[i] = (randomGenerator.nextFloat()-0.5f)*(Gdx.graphics.getHeight()/2 - gap);
				} else tubeX[i] -= tubeVelocity;

				batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);
				batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i]);

				topTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
				bottomTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());
			}

			if (birdY > 0 && birdY < Gdx.graphics.getHeight()) {
				velocity += gravity;
				birdY -= velocity;
			} else gameState = 2;

		} else if (gameState == 0){
			info.draw(batch, "Tap anywhere", Gdx.graphics.getWidth()/2-250,Gdx.graphics.getHeight()-150);
			info.draw(batch, "to start!", Gdx.graphics.getWidth()/2-150 ,Gdx.graphics.getHeight()-250);
		    if (Gdx.input.justTouched()) gameState = 1;
        } else if (gameState==2){
			info.draw(batch, "Tap anywhere", Gdx.graphics.getWidth()/2-250,Gdx.graphics.getHeight()-150);
			info.draw(batch, "to restart!", Gdx.graphics.getWidth()/2-150 ,Gdx.graphics.getHeight()-250);

			font.draw(batch, String.valueOf(score), 100, 200);

			if (birdY>0) {
				velocity += gravity;
				birdY-=velocity;
			}
		    batch.draw(gameover, Gdx.graphics.getWidth()/2 - gameover.getWidth()/2, Gdx.graphics.getHeight()/2 - gameover.getHeight()/2);
			if (Gdx.input.justTouched()) {
				score = 0;
				scoringTube = 0;
				velocity = 0;
				startGame();
				gameState = 3;
			}
		}


		if (flapState == 0) flapState = 1;
		else flapState = 0;

		batch.draw(birds[flapState], Gdx.graphics.getWidth() / 2 - birds[flapState].getWidth() / 2, birdY);
		font.draw(batch, String.valueOf(score), 100, 200);
		birdCircle.set(Gdx.graphics.getWidth() / 2, birdY + birds[flapState].getHeight() / 2, birds[flapState].getWidth()/2);
		batch.end();

//		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//		shapeRenderer.setColor(Color.RED);
//		shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);

		for (int i = 0; i < numberOfTubes; i++) {

//			shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
//			shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());
			if (Intersector.overlaps(birdCircle, bottomTubeRectangles[i]) || Intersector.overlaps(birdCircle, topTubeRectangles[i])){
				gameState = 2;
            }
		}

//		shapeRenderer.end();

	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
