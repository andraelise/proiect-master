package com.example.proiectmaster.DAO;

public class FirebaseDAOFactory extends DAOFactory {

    public PacientDAO getPacientDAO() {
        return new FirebasePacientDAO();
    }
}