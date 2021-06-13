package fr.imt.albi.pacman.model;

import fr.imt.albi.pacman.main.PacManLauncher;
import fr.imt.albi.pacman.utils.ArcCircle;
import fr.imt.albi.pacman.utils.Figure;
import fr.imt.albi.pacman.utils.Food;
import fr.imt.albi.pacman.utils.Wall;

public class Pacman extends Creature {
	/* L'angle d'ouverture mini de sa bouche quand il avance */
	public static final int MIN_MOUTH_ANGLE = 10;
	/* L'angle d'ouverture maxi de sa bouche quand il avance */
	public static final int MAX_MOUTH_ANGLE = 40;
	/* Sa vitesse sur la grille */
	public static final int SPEED_PACMAN = 20;
	/* La couleur de Pacman */
	private static final String PACMAN_COLOR = "yellow";
	/* Le nombre initial de vies de Pacman */
	private static final int LIFE_START = 3;
	/* Le nombre de points pour qu'il obtienne une vie */
	private static final int LIFE_POINT_THRESHOLD = 10000;

	private final ArcCircle pacman;
	private int mouthAngle;
	private boolean isMouthOpen;
	private boolean isEmpowered;
	private String lastPosition;
	private String lastMovement;
	private int currentLife;
	private int currentScore;
	private int nextLifeThreshold;
	public int given_Life = 0;

	public Pacman(int size, int x, int y) {
		this.pacman = new ArcCircle(size, x, y, PACMAN_COLOR, 0, 360);

		this.lastPosition = PacManLauncher.LEFT;
		this.mouthAngle = MIN_MOUTH_ANGLE;
		this.handleMouthOpening(PacManLauncher.LEFT);
		this.currentLife = LIFE_START;
		this.isEmpowered = false;
		this.lastMovement = PacManLauncher.LEFT;
		this.nextLifeThreshold = Pacman.LIFE_POINT_THRESHOLD;
	}

	public void removeLife() {
		// TODO Méthode qui gère le retrait d'une vie à Pacman
		this.currentLife--;
	}

	public int getCurrentLife() {
		return this.currentLife;
	}

	public void updateScoreFood() {
		// TODO Là, si Pacman a reçu un power-up, faut incrémenter le score comme il se
		// doit.
		this.currentScore += Food.POWER_UP_SCORE;
		this.checkIfNewLife();
	}

	private void checkIfNewLife() {
		// TODO Là, faut vérifier si le Pacman a atteint la limite pour avoir une vie
		// supplémentaire :)
		if(this.currentScore >= this.nextLifeThreshold) {
			this.currentLife++;
			this.nextLifeThreshold += LIFE_POINT_THRESHOLD;
	}
	}
	
	
	public void updateScoreGhost() {
		// TODO Là, si Pacman bouffe un fantome, faut incrémenter le score comme il faut
		// aussi.
		this.currentScore += Ghost.GHOST_SCORE;
		this.checkIfNewLife();
	}

	public int getCurrentScore() {
		return this.currentScore;
	}

	@Override
	public int getSpeed() {
		return Pacman.SPEED_PACMAN;
	}

	@Override
	public int getX() {
		return this.pacman.getX();
	}

	@Override
	public int getY() {
		return this.pacman.getY();
	}

	@Override
	public int getWidth() {
		return this.pacman.getWidth();
	}

	@Override
	public void draw() {
		this.pacman.draw();
	}

	@Override
	public void move(String direction) {
		int xMove = 0;
		int yMove = 0;

		if (this.isMovePossible(direction)) {
			/*
			 * TODO Si le déplacement est possible, il faut : - récupérer les nouvelles
			 * coordonnées, - voir avec quoi on risque de se percuter avec ces nouvelles
			 * coordonnées et agir en conséquence (i.e. remettre à jour les coords) - se
			 * déplacer - garder une trace du dernier déplacement effectué (y a un attribut
			 * de classe pour ça) - Animer sa bouche ;)
			 */
			int[] mouvement = this.navigateInMap(direction);
			this.handleMouthOpening(direction);
			this.animateMouth();
			this.move(mouvement[0], mouvement[1]);
			this.lastPosition = direction;
			this.lastMovement = direction;

			
			
		} else {
			/*
			 * TODO Si le déplacement n'est possible, il faut pouvoir récupérer les
			 * coordonnées en partant du principe que sa direction sera égale à la dernière
			 * direction qui avait marché. Quasiment la même chose, juste que ça sera pas
			 * direction qui sera utilisé, mais autre chose :) Faut toujours animer sa
			 * bouche ceci dit !
			 */
			direction = this.lastPosition;
			int[] mouvement = this.navigateInMap(direction);
			this.handleMouthOpening(direction);
			this.animateMouth();
			this.move(mouvement[0], mouvement[1]);
			this.lastPosition = direction;
			this.lastMovement = direction;
		}
	}

	@Override
	public void move(int xMove, int yMove) {
		this.pacman.move(xMove, yMove);
	}

	/**
	 * Anime la bouche du petit aussi, mais avec les calculs qui vont bien
	 *
	 * @param direction La direction à laquelle pointe Pacman
	 */
	private void handleMouthOpening(String direction) {
		int startAngle = 0;
		int extentAngle = 0;

		if (direction.equals(PacManLauncher.UP)) {
			startAngle = 90 - this.mouthAngle;
			extentAngle = -360 + 2 * this.mouthAngle;
		} else if (direction.equals(PacManLauncher.LEFT)) {
			startAngle = 180 - this.mouthAngle;
			extentAngle = -360 + 2 * this.mouthAngle;
		} else if (direction.equals(PacManLauncher.DOWN)) {
			startAngle = 270 - this.mouthAngle;
			extentAngle = -360 + 2 * this.mouthAngle;
		} else if (direction.equals(PacManLauncher.RIGHT)) {
			startAngle = -this.mouthAngle;
			extentAngle = -360 + 2 * this.mouthAngle;
		}

		this.pacman.setAngleStart(startAngle);
		this.pacman.setAngleExtent(extentAngle);
		this.lastPosition = direction;
	}

	@Override
	protected void interactWithFood(Figure[][] map, int i, int j) {
		Figure f = map[i][j];
		if (f instanceof Food) {
			Food food = (Food) f;
			if (food.getFood() != null) {
				/*
				 * TODO Ici, il faut: - Changer le food en null (y a un setFood...) - Redessiner
				 * le food (.draw()) - Et après, remettre à jour la map en updatant la bouffe
				 * qu'il y avait dedans - Mettre à jour le score - Sachant qu'un food peut être
				 * un powerup, y a un truc à gérer :)
				 */
				food.setFood(null);
				food.draw();
				if (food.isPowerUp()) {
					this.isEmpowered = true;
				}
				this.updateScoreFood();
				this.gameMap.pickFood();
			}
		}
	}

	public boolean getIsEmpowered() {
		return this.isEmpowered;
	}

	public void resetIsEmpowered() {
		this.isEmpowered = false;
	}

	@Override
	public boolean checkCaseType(Figure f) {
		return f instanceof Wall || f instanceof Food;
	}

	/**
	 * Anime la bouche du petit.
	 */
	public void animateMouth() {
		if (this.isMouthOpen) {
			this.mouthAngle = MIN_MOUTH_ANGLE;
		} else {
			this.mouthAngle = MAX_MOUTH_ANGLE;
		}
		this.handleMouthOpening(this.lastPosition);
		this.isMouthOpen = !this.isMouthOpen;
	}

	/**
	 * Méthode qui permet de dire s'il se pète la gueule avec un fantome.
	 *
	 * @param f Le fantome en question
	 * @return true ou false
	 */
	public boolean isPacmanCollidingWithGhost(Ghost f) {
		int xGhost = f.getX();
		int yGhost = f.getY();
		int sizeGhost = f.getWidth();

		int xPacman = this.getX();
		int yPacman = this.getY();
		int sizePacman = this.getWidth();

		boolean posMinX = xPacman < xGhost + sizeGhost || xPacman + sizePacman < xGhost + sizeGhost;
		boolean posMaxX = xPacman > xGhost || xPacman + sizePacman > xGhost;
		boolean posMinY = yPacman < yGhost + sizeGhost || yPacman + sizePacman < yGhost + sizeGhost;
		boolean posMaxY = yPacman > yGhost || yPacman + sizePacman > yGhost;

		return posMinX && posMaxX && posMinY && posMaxY;
	}
}
