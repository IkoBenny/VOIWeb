/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.desktop.model.ReasonForInterest;
import com.desktop.model.VehicleMake;
import com.desktop.model.VehicleModel;
import com.desktop.model.VehicleOfInterest;
import controller.exceptions.NonexistentEntityException;
import controller.exceptions.PreexistingEntityException;
import controller.exceptions.RollbackFailureException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author benny
 */
public class VehicleOfInterestJpaController implements Serializable {

    public VehicleOfInterestJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(VehicleOfInterest vehicleOfInterest) throws PreexistingEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            ReasonForInterest reason = vehicleOfInterest.getReason();
            if (reason != null) {
                reason = em.getReference(reason.getClass(), reason.getReason());
                vehicleOfInterest.setReason(reason);
            }
            VehicleMake make = vehicleOfInterest.getMake();
            if (make != null) {
                make = em.getReference(make.getClass(), make.getMake());
                vehicleOfInterest.setMake(make);
            }
            VehicleModel model = vehicleOfInterest.getModel();
            if (model != null) {
                model = em.getReference(model.getClass(), model.getModel());
                vehicleOfInterest.setModel(model);
            }
            em.persist(vehicleOfInterest);
            if (reason != null) {
                reason.getVehicleOfInterestCollection().add(vehicleOfInterest);
                reason = em.merge(reason);
            }
            if (make != null) {
                make.getVehicleOfInterestCollection().add(vehicleOfInterest);
                make = em.merge(make);
            }
            if (model != null) {
                model.getVehicleOfInterestCollection().add(vehicleOfInterest);
                model = em.merge(model);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findVehicleOfInterest(vehicleOfInterest.getLicensePlate()) != null) {
                throw new PreexistingEntityException("VehicleOfInterest " + vehicleOfInterest + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(VehicleOfInterest vehicleOfInterest) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            VehicleOfInterest persistentVehicleOfInterest = em.find(VehicleOfInterest.class, vehicleOfInterest.getLicensePlate());
            ReasonForInterest reasonOld = persistentVehicleOfInterest.getReason();
            ReasonForInterest reasonNew = vehicleOfInterest.getReason();
            VehicleMake makeOld = persistentVehicleOfInterest.getMake();
            VehicleMake makeNew = vehicleOfInterest.getMake();
            VehicleModel modelOld = persistentVehicleOfInterest.getModel();
            VehicleModel modelNew = vehicleOfInterest.getModel();
            if (reasonNew != null) {
                reasonNew = em.getReference(reasonNew.getClass(), reasonNew.getReason());
                vehicleOfInterest.setReason(reasonNew);
            }
            if (makeNew != null) {
                makeNew = em.getReference(makeNew.getClass(), makeNew.getMake());
                vehicleOfInterest.setMake(makeNew);
            }
            if (modelNew != null) {
                modelNew = em.getReference(modelNew.getClass(), modelNew.getModel());
                vehicleOfInterest.setModel(modelNew);
            }
            vehicleOfInterest = em.merge(vehicleOfInterest);
            if (reasonOld != null && !reasonOld.equals(reasonNew)) {
                reasonOld.getVehicleOfInterestCollection().remove(vehicleOfInterest);
                reasonOld = em.merge(reasonOld);
            }
            if (reasonNew != null && !reasonNew.equals(reasonOld)) {
                reasonNew.getVehicleOfInterestCollection().add(vehicleOfInterest);
                reasonNew = em.merge(reasonNew);
            }
            if (makeOld != null && !makeOld.equals(makeNew)) {
                makeOld.getVehicleOfInterestCollection().remove(vehicleOfInterest);
                makeOld = em.merge(makeOld);
            }
            if (makeNew != null && !makeNew.equals(makeOld)) {
                makeNew.getVehicleOfInterestCollection().add(vehicleOfInterest);
                makeNew = em.merge(makeNew);
            }
            if (modelOld != null && !modelOld.equals(modelNew)) {
                modelOld.getVehicleOfInterestCollection().remove(vehicleOfInterest);
                modelOld = em.merge(modelOld);
            }
            if (modelNew != null && !modelNew.equals(modelOld)) {
                modelNew.getVehicleOfInterestCollection().add(vehicleOfInterest);
                modelNew = em.merge(modelNew);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = vehicleOfInterest.getLicensePlate();
                if (findVehicleOfInterest(id) == null) {
                    throw new NonexistentEntityException("The vehicleOfInterest with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            VehicleOfInterest vehicleOfInterest;
            try {
                vehicleOfInterest = em.getReference(VehicleOfInterest.class, id);
                vehicleOfInterest.getLicensePlate();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The vehicleOfInterest with id " + id + " no longer exists.", enfe);
            }
            ReasonForInterest reason = vehicleOfInterest.getReason();
            if (reason != null) {
                reason.getVehicleOfInterestCollection().remove(vehicleOfInterest);
                reason = em.merge(reason);
            }
            VehicleMake make = vehicleOfInterest.getMake();
            if (make != null) {
                make.getVehicleOfInterestCollection().remove(vehicleOfInterest);
                make = em.merge(make);
            }
            VehicleModel model = vehicleOfInterest.getModel();
            if (model != null) {
                model.getVehicleOfInterestCollection().remove(vehicleOfInterest);
                model = em.merge(model);
            }
            em.remove(vehicleOfInterest);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<VehicleOfInterest> findVehicleOfInterestEntities() {
        return findVehicleOfInterestEntities(true, -1, -1);
    }

    public List<VehicleOfInterest> findVehicleOfInterestEntities(int maxResults, int firstResult) {
        return findVehicleOfInterestEntities(false, maxResults, firstResult);
    }

    private List<VehicleOfInterest> findVehicleOfInterestEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(VehicleOfInterest.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public VehicleOfInterest findVehicleOfInterest(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(VehicleOfInterest.class, id);
        } finally {
            em.close();
        }
    }

    public int getVehicleOfInterestCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<VehicleOfInterest> rt = cq.from(VehicleOfInterest.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
