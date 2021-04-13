package service.impl;

import dao.UniversityDao;
import dao.impl.UniversityDaoImpl;
import domain.University;
import service.UniversityService;

public class UniversityServiceImpl implements UniversityService {
    private final UniversityDao dao = new UniversityDaoImpl();

    @Override
    public University login(University university) {
        return dao.findUniversityByEmailAndPassword(university.getEmail(), university.getPassword());
    }

    @Override
    public boolean addUniversity(University university) {
        return dao.addUniversity(university);
    }
}
