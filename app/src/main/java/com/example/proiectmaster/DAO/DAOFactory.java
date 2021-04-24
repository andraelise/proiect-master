package com.example.proiectmaster.DAO;

public abstract class DAOFactory {
    public static final int FIRE_BASE = 1;
    public abstract PacientDAO getPacientDAO();

    public static DAOFactory getDAOFactory(int whichFactory){
        switch (whichFactory) {
            case FIRE_BASE:
                return new FirebaseDAOFactory();
            default:
                return null;
        }
    }
}
