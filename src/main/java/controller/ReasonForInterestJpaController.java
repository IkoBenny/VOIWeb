/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import com.desktop.model.ReasonForInterest;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.desktop.model.VehicleOfInterest;
import controller.exceptions.IllegalOrphanException;
import controller.exceptions.NonexistentEntityException;
import controller.exceptions.PreexistingEntityException;
import controller.exceptions.RollbackFailureException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author benny
 */
public class ReasonForInterestJpaController implements Serializable {

    public ReasonForInterestJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(ReasonForInterest reasonForInterest) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (reasonForInterest.getVehicleOfInterestCollection() == null) {
            reasonForInterest.setVehicleOfInterestCollection(new ArrayList<VehicleOfInterest>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Collection<VehicleOfInterest> attachedVehicleOfInterestCollection = new ArrayList<VehicleOfInterest>();
            for (VehicleOfInterest vehicleOfInterestCollectionVehicleOfInterestToAttach : reasonForInterest.getVehicleOfInterestCollection()) {
                vehicleOfInterestCollectionVehicleOfInterestToAttach = em.getReference(vehicleOfInterestCollectionVehicleOfInterestToAttach.getClass(), vehicleOfInterestCollectionVehicleOfInterestToAttach.getLicensePlate());
                attachedVehicleOfInterestCollection.add(vehicleOfInterestCollectionVehicleOfInterestToAttach);
            }
            reasonForInterest.setVehicleOfInterestCollection(attachedVehicleOfInterestCollection);
            em.persist(reasonForInterest);
            for (VehicleOfInterest vehicleOfInterestCollectionVehicleOfInterest : reasonForInterest.getVehicleOfInterestCollection()) {
                ReasonForInterest oldReasonOfVehicleOfInterestCollectionVehicleOfInterest = vehicleOfInterestCollectionVehicleOfInterest.getReason();
                vehicleOfInterestCollectionVehicleOfInterest.setReason(reasonForInterest);
                vehicleOfInterestCollectionVehicleOfInterest = em.merge(vehicleOfInterestCollectionVehicleOfInterest);
                if (oldReasonOfVehicleOfInterestCollectionVehicleOfInterest != null) {
                    oldReasonOfVehicleOfInterestCollectionVehicleOfInterest.getVehicleOfInterestCollection().remove(vehicleOfInterestCollectionVehicleOfInterest);
                    oldReasonOfVehicleOfInterestCollectionVehicleOfInterest = em.merge(oldReasonOfVehicleOfInterestCollectionVehicleOfInterest);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findReasonForInterest(reasonForInterest.getReason()) != null) {
                throw new PreexistingEntityException("ReasonForInterest " + reasonForInterest + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(ReasonForInterest reasonForInterest) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            ReasonForInterest persistentReasonForInterest = em.find(ReasonForInterest.class, reasonForInterest.getReason());
            Collection<VehicleOfInterest> vehicleOfInterestCollectionOld = persistentReasonForInterest.getVehicleOfInterestCollection();
            Collection<VehicleOfInterest> vehicleOfInterestCollectionNew = reasonForInterest.getVehicleOfInterestCollection();
            List<String> illegalOrphanMessages = null;
            for (VehicleOfInterest vehicleOfInterestCollectionOldVehicleOfInterest : vehicleOfInterestCollectionOld) {
                if (!vehicleOfInterestCollectionNew.contains(vehicleOfInterestCollectionOldVehicleOfInterest)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain VehicleOfInterest " + vehicleOfInterestCollectionOldVehicleOfInterest + " since its reason field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<VehicleOfInterest> attachedVehicleOfInterestCollectionNew = new ArrayList<VehicleOfInterest>();
            for (VehicleOfInterest vehicleOfInterestCollectionNewVehicleOfInterestToAttach : vehicleOfInterestCollectionNew) {
                vehicleOfInterestCollectionNewVehicleOfInterestToAttach = em.getReference(vehicleOfInterestCollectionNewVehicleOfInterestToAttach.getClass(), vehicleOfInterestCollectionNewVehicleOfInterestToAttach.getLicensePlate());
                attachedVehicleOfInterestCollectionNew.add(vehicleOfInterestCollectionNewVehicleOfInterestToAttach);
            }
            vehicleOfInterestCollectionNew = attachedVehicleOfInterestCollectionNew;
            reasonForInterest.setVehicleOfInterestCollection(vehicleOfInterestCollectionNew);
            reasonForInterest = em.merge(reasonForInterest);
            for (VehicleOfInterest vehicleOfInterestCollectionNewVehicleOfInterest : vehicleOfInterestCollectionNew) {
                if (!vehicleOfInterestCollectionOld.contains(vehicleOfInterestCollectionNewVehicleOfInterest)) {
                    ReasonForInterest oldReasonOfVehicleOfInterestCollectionNewVehicleOfInterest = vehicleOfInterestCollectionNewVehicleOfInterest.getReason();
                    vehicleOfInterestCollectionNewVehicleOfInterest.setReason(reasonForInterest);
                    vehicleOfInterestCollectionNewVehicleOfInterest = em.merge(vehicleOfInterestCollectionNewVehicleOfInterest);
                    if (oldReasonOfVehicleOfInterestCollectionNewVehicleOfInterest != null && !oldReasonOfVehicleOfInterestCollectionNewVehicleOfInterest.equals(reasonForInterest)) {
                        oldReasonOfVehicleOfInterestCollectionNewVehicleOfInterest.getVehicleOfInterestCollection().remove(vehicleOfInterestCollectionNewVehicleOfInterest);
                        oldReasonOfVehicleOfInterestCollectionNewVehicleOfInterest = em.merge(oldReasonOfVehicleOfInterestCollectionNewVehicleOfInterest);
                    }
                }
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
                String id = reasonForInterest.getReason();
                if (findReasonForInterest(id) == null) {
                    throw new NonexistentEntityException("The reasonForInterest with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            ReasonForInterest reasonForInterest;
            try {
                reasonForInterest = em.getReference(ReasonForInterest.class, id);
                reasonForInterest.getReason();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The reasonForInterest with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<VehicleOfInterest> vehicleOfInterestCollectionOrphanCheck = reasonForInterest.getVehicleOfInterestCollection();
            for (VehicleOfInterest vehicleOfInterestCollectionOrphanCheckVehicleOfInterest : vehicleOfInterestCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This ReasonForInterest (" + reasonForInterest + ") cannot be destroyed since the VehicleOfInterest " + vehicleOfInterestCollectionOrphanCheckVehicleOfInterest + " in its vehicleOfInterestCollection field has a non-nullable reason field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(reasonForInterest);
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

    public List<ReasonForInterest> findReasonForInterestEntities() {
        return findReasonForInterestEntities(true, -1, -1);
    }

    public List<ReasonForInterest> findReasonForInterestEntities(int maxResults, int firstResult) {
        return findReasonForInterestEntities(false, maxResults, firstResult);
    }

    private List<ReasonForInterest> findReasonForInterestEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(ReasonForInterest.class));
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

    public ReasonForInterest findReasonForInterest(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(ReasonForInterest.class, id);
        } finally {
            em.close();
        }
    }

    public int getReasonForInterestCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<ReasonForInterest> rt = cq.from(ReasonForInterest.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
