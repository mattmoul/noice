package fr.imt.albi.pacman.model;

import fr.imt.albi.pacman.main.PacManLauncher;
import fr.imt.albi.pacman.utils.Figure;
import fr.imt.albi.pacman.utils.GhostSkin;
import fr.imt.albi.pacman.utils.Wall;

import java.util.ArrayList;

public class Ghost extends Creature {

    public static final int SPEED_GHOST = 10;
    public static final int GHOST_SCORE = 100;
    /* Nombre de tour où le déplacement du fantôme sera aléatoire */
    public static final int STUCK_COOLDOWN = 30;
    /* Distance maximale à laquelle le fantôme va traquer le PacMan */
    public static final double LIM_DIST = 150.0;
    private final GhostSkin ghostSkin;
    private final String ghostColor;
    private String previousMove;
    private String directionX;
    private String directionY;
    private int counterUTurn;
    private int counterFear;
    /* Compteur du nombre de tour restant en état Stuck (déplacement aléatoire) */
    private int counterStuck;

    public Ghost(int size, int x, int y, String color) {
        this.previousMove = PacManLauncher.UP;
        this.initUTurnCounter();

        this.counterFear = 0;
        this.ghostColor = color;
        
        /* Initialement les fantômes sont stuck pour qu'ils se séparent */
        this.counterStuck = 20;

        this.ghostSkin = new GhostSkin(size, x, y, color);
    }

    public void move(int PacPosX, int PacPosY, boolean tracking) {
        if (this.counterFear == 0) {
            this.setNormalState();
        }
        
        /* Calcul de la distance au PacMan en X, Y, et directe */
        int GhostPosX = this.getX();
    	int GhostPosY = this.getY();
    	int diffX = GhostPosX - PacPosX;
    	int diffY = GhostPosY - PacPosY;
    	double dist_tot = Math.sqrt(Math.pow(diffX, 2) + Math.pow(diffY, 2));
        
        //Tracking off OU distance au PacMan trop grande OU le ghost est "stuck"
        if (!tracking || dist_tot > LIM_DIST || this.counterStuck > 0) {
        	
	        if (this.counterFear % 2 == 1 || this.counterFear == 0) {
	            if (this.counterFear > 0) {
	                this.counterFear--;
	            }
	         
	        /* Décrémentation de l'état stuck du fantôme */
	        if (this.counterStuck > 0) {
	        	this.counterStuck--;
	        }
	        
	            this.counterUTurn--;
	            if (this.counterUTurn == 0) {
	                switch (this.previousMove) {
	                    case PacManLauncher.UP:
	                        this.move(PacManLauncher.DOWN);
	                        break;
	                    case PacManLauncher.DOWN:
	                        this.move(PacManLauncher.UP);
	                        break;
	                    case PacManLauncher.LEFT:
	                        this.move(PacManLauncher.RIGHT);
	                        break;
	                    case PacManLauncher.RIGHT:
	                        this.move(PacManLauncher.LEFT);
	                        break;
	                }
	                this.initUTurnCounter();
	            } else {
	                checkCrossing(this.previousMove);
	            }
	        } else {
	            this.counterFear--;
	        }
	        
	    //Tracking activé
	        
        } else {
        	if (this.counterFear == 0) {
                this.setNormalState();
        	}
        	
        	if (this.counterFear % 2 == 1 || this.counterFear == 0) {
        		
        		// Le fantôme fuit le PacMan
	            if (this.counterFear > 0) {
	            	
	            	/* On cherche à augmenter les distances en X et en Y
	            	 * On calcule dans quelle direction en X et en Y le fantôme doit s'échapper
	            	 * On augmente d'abord l'écart de distance le plus faible (en X ou en Y) */ 
	            	if (GhostPosX < PacPosX) {
            			this.directionX = PacManLauncher.LEFT;
            		} else {
            			this.directionX = PacManLauncher.RIGHT;
            		}
	            	
	            	if (GhostPosY < PacPosY) {
            			this.directionY = PacManLauncher.UP;
            		} else {
            			this.directionY = PacManLauncher.DOWN;
            		}
	            	
	            	//Cas dx < dy
	            	if (Math.abs(diffX) < Math.abs(diffY)) {
	            		
	            		/* On vérifie si on peut augmenter la distance la plus courte */
	            		if (this.isMovePossible(this.directionX)) {
	            			this.move(this.directionX);
	            			this.previousMove = directionX;
	            		
	            		/* Sinon, on vérifie si on peut augmenter la distance la plus longue */
	            		} else if (this.isMovePossible(this.directionY)) {
	            			this.move(this.directionY);
	            			this.previousMove = directionY;
	            		
	            		/* Sinon, on se déplace dans l'une des directions restantes*/
	            		} else if (this.directionX == PacManLauncher.LEFT) {
	            			this.move(PacManLauncher.RIGHT);
	            			this.previousMove = PacManLauncher.RIGHT;
	            			
	            		} else {
	            			this.move(PacManLauncher.LEFT);
	            			this.previousMove = PacManLauncher.LEFT;
	            		}
	            	
	            	//Cas dy < dx
	            	} else {
	            		
	            		if (this.isMovePossible(this.directionY)) {
	            			this.move(this.directionY);
	            			this.previousMove = directionY;
	            			
	            		} else if (this.isMovePossible(this.directionX)) {
	            			this.move(this.directionX);
	            			this.previousMove = directionX;
	            		
	            		} else if (this.directionY == PacManLauncher.UP) {
	            			this.move(PacManLauncher.DOWN);
	            			this.previousMove = PacManLauncher.DOWN;
	            			
	            		} else {
	            			this.move(PacManLauncher.UP);
	            			this.previousMove = PacManLauncher.UP;
	            		}	
	            	}
	            	this.counterFear--;
	            
	            // Le fantôme chasse le PacMan
	            } else {           	
	            	
	            	if (GhostPosX < PacPosX) {
            			this.directionX = PacManLauncher.RIGHT;
            		} else {
            			this.directionX = PacManLauncher.LEFT;
            		}
	            	
	            	if (GhostPosY < PacPosY) {
            			this.directionY = PacManLauncher.DOWN;
            		} else {
            			this.directionY = PacManLauncher.UP;
            		}
	            	
	            	//Cas dx > dy
	            	if (Math.abs(diffX) > Math.abs(diffY)) {
	            		
	            		if (this.isMovePossible(this.directionX)) {
	            			this.move(this.directionX);
	            			this.previousMove = directionX;
	            			
	            		} else if (this.isMovePossible(this.directionY)) {
	            			this.move(this.directionY);
	            			this.previousMove = directionY;
	            		
	            		} else if (this.directionY == PacManLauncher.UP) {
	            			this.move(PacManLauncher.DOWN);
	            			this.previousMove = PacManLauncher.DOWN;
	            			this.setStuckState();
	            			
	            		} else {
	            			this.move(PacManLauncher.UP);
	            			this.previousMove = PacManLauncher.UP;
	            			this.setStuckState();
	            		}
	            	
	            	//Cas dy > dx
	            	} else {
	            		if (this.isMovePossible(this.directionY)) {
	            			this.move(this.directionY);
	            			this.previousMove = directionY;
	            			
	            		} else if (this.isMovePossible(this.directionX)) {
	            			this.move(this.directionX);
	            			this.previousMove = directionX;
	            		
	            		} else if (this.directionX == PacManLauncher.RIGHT) {
	            			this.move(PacManLauncher.LEFT);
	            			this.previousMove = PacManLauncher.LEFT;
	            			this.setStuckState();
	            			
	            		} else {
	            			this.move(PacManLauncher.RIGHT);
	            			this.previousMove = PacManLauncher.RIGHT;
	            			this.setStuckState();
	            		}	
	            	}
	            	
	            	int NewGhostPosX = this.getX();
	            	int NewGhostPosY = this.getY();
	            	
	            	/* Si le fantôme n'a pas bougé, il est stuck et aura des mouvements aléatoires */
	            	if (NewGhostPosX == GhostPosX && NewGhostPosY == GhostPosY) {
	            		this.setStuckState();
	            	}
	            }
        	} else {
        		this.counterFear--;
        	}
        }
    }

    public void initUTurnCounter() {
        this.counterUTurn = (int) (Math.random() * 30) + 20;
    }

    public void move(String direction) {
        this.previousMove = direction;
        int xMove = 0;
        int yMove = 0;

        int[] crossMap = this.navigateInMap(direction);
        xMove = crossMap[0];
        yMove = crossMap[1];

        crossMap = this.checkCollision(direction, xMove, yMove);
        xMove = crossMap[0];
        yMove = crossMap[1];

        this.move(xMove, yMove);
    }

    public void move(int dx, int dy) {
        for (Figure figure : this.getSkin()) {
            figure.move(dx, dy);
        }
    }

    private Figure[] getSkin() {
        return this.ghostSkin.getFigures();
    }

    public void setFearState() {
        this.counterFear = 60;
        Figure[] figures = this.getSkin();
        for (int i = 0; i < 5; i++) {
            figures[i].setColor("blue");
        }
    }
    
    public void setStuckState() {
    	this.counterStuck = this.STUCK_COOLDOWN;
    }

    public void setNormalState() {
        this.counterFear = 0;
        Figure[] figures = this.getSkin();
        for (int i = 0; i < 5; i++) {
            figures[i].setColor(this.ghostColor);
        }
    }

    public int getX() {
        return this.ghostSkin.getX();
    }

    public int getY() {
        return this.ghostSkin.getY();
    }

    public int getWidth() {
        return this.ghostSkin.getWidth();
    }

    public int getSpeed() {
        return Ghost.SPEED_GHOST;
    }

    public int getFearCounter() {
        return this.counterFear;
    }

    public void checkCrossing(String toward) {
        boolean haveMoved = false;
        Figure[][] map = this.gameMap.getMap();

        if (this.getX() % this.gameMap.getSizeCase() == 0 && this.getY() % this.gameMap.getSizeCase() == 0) {
            int[] position = this.getColumnAndRow();
            int xPos = position[0];
            int yPos = position[1];

            Figure fUp = map[yPos - 1][xPos];
            Figure fDown = map[yPos + 1][xPos];
            Figure fLeft = map[yPos][xPos - 1];
            Figure fRight = map[yPos][xPos + 1];

            ArrayList<Figure> caseAround = new ArrayList<Figure>();
            caseAround.add(fUp);
            caseAround.add(fDown);
            caseAround.add(fLeft);
            caseAround.add(fRight);

            switch (toward) {
                case PacManLauncher.UP:
                    if (fLeft.getClass().getName().compareTo("view.Wall") != 0 || fRight.getClass().getName().compareTo("view.Wall") != 0) {
                        caseAround.remove(fDown);
                        this.chooseMove(toward, caseAround, fUp, fDown, fLeft, fRight);
                        haveMoved = true;
                    } else if (fUp.getClass().getName().compareTo("view.Wall") == 0) {
                        this.chooseMove(toward, caseAround, fUp, fDown, fLeft, fRight);
                        haveMoved = true;
                    }
                    break;
                case PacManLauncher.DOWN:
                    if (fLeft.getClass().getName().compareTo("view.Wall") != 0 || fRight.getClass().getName().compareTo("view.Wall") != 0) {
                        caseAround.remove(fUp);
                        this.chooseMove(toward, caseAround, fUp, fDown, fLeft, fRight);
                        haveMoved = true;
                    } else if (fDown.getClass().getName().compareTo("view.Wall") == 0) {
                        this.chooseMove(toward, caseAround, fUp, fDown, fLeft, fRight);
                        haveMoved = true;
                    }
                    break;
                case PacManLauncher.LEFT:
                    if (fUp.getClass().getName().compareTo("view.Wall") != 0 || fDown.getClass().getName().compareTo("view.Wall") != 0) {
                        caseAround.remove(fRight);
                        this.chooseMove(toward, caseAround, fUp, fDown, fLeft, fRight);
                        haveMoved = true;
                    } else if (fLeft.getClass().getName().compareTo("view.Wall") == 0) {
                        this.chooseMove(toward, caseAround, fUp, fDown, fLeft, fRight);
                        haveMoved = true;
                    }
                    break;
                case PacManLauncher.RIGHT:
                    if (fUp.getClass().getName().compareTo("view.Wall") != 0 || fDown.getClass().getName().compareTo("view.Wall") != 0) {
                        caseAround.remove(fLeft);
                        this.chooseMove(toward, caseAround, fUp, fDown, fLeft, fRight);
                        haveMoved = true;
                    } else if (fRight.getClass().getName().compareTo("view.Wall") == 0) {
                        this.chooseMove(toward, caseAround, fUp, fDown, fLeft, fRight);
                        haveMoved = true;
                    }
                    break;
            }
        }

        if (!haveMoved) {
            this.move(this.previousMove);
        }
    }

    public void chooseMove(String toward, ArrayList<Figure> listF, Figure fUp, Figure fDown, Figure fLeft, Figure fRight) {
        boolean result = false;
        ArrayList<Figure> toGo = new ArrayList<Figure>();

        for (Figure f : listF) {
            if (!(f instanceof Wall)) {
                toGo.add(f);
            }
        }

        Figure nextMove = null;
        double ran = Math.random() * toGo.size();
        for (int i = 0; i < toGo.size(); i++) {
            if (ran >= i && ran < i + 1) {
                nextMove = toGo.get(i);
            }
        }

        if (nextMove == null) {
            this.move(toward);
        } else if (nextMove == fUp) {
            this.move(PacManLauncher.UP);
        } else if (nextMove == fDown) {
            this.move(PacManLauncher.DOWN);
        } else if (nextMove == fLeft) {
            this.move(PacManLauncher.LEFT);
        } else if (nextMove == fRight) {
            this.move(PacManLauncher.RIGHT);
        }
    }

    public boolean checkCaseType(Figure f) {
        return (f instanceof Wall);
    }

    protected void interactWithFood(Figure[][] map, int i, int j) {
    }

    public void draw() {
        this.ghostSkin.draw();
    }
}
