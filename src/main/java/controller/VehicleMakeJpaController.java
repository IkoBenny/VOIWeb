/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import com.desktop.model.VehicleMake;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.desktop.model.VehicleOfInterest;
import java.util.ArrayList;
import java.util.Collection;
import com.desktop.model.VehicleModel;
import controller.exceptions.IllegalOrphanException;
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
public class VehicleMakeJpaController implements Serializable {

    public VehicleMakeJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(VehicleMake vehicleMake) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (vehicleMake.getVehicleOfInterestCollection() == null) {
            vehicleMake.setVehicleOfInterestCollection(new ArrayList<VehicleOfInterest>());
        }
        if (vehicleMake.getVehicleModelCollection() == null) {
            vehicleMake.setVehicleModelCollection(new ArrayList<VehicleModel>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Collection<VehicleOfInterest> attachedVehicleOfInterestCollection = new ArrayList<VehicleOfInterest>();
            for (VehicleOfInterest vehicleOfInterestCollectionVehicleOfInterestToAttach : vehicleMake.getVehicleOfInterestCollection()) {
                vehicleOfInterestCollectionVehicleOfInterestToAttach = em.getReference(vehicleOfInterestCollectionVehicleOfInterestToAttach.getClass(), vehicleOfInterestCollectionVehicleOfInterestToAttach.getLicensePlate());
                attachedVehicleOfInterestCollection.add(vehicleOfInterestCollectionVehicleOfInterestToAttach);
            }
            vehicleMake.setVehicleOfInterestCollection(attachedVehicleOfInterestCollection);
            Collection<VehicleModel> attachedVehicleModelCollection = new ArrayList<VehicleModel>();
            for (VehicleModel vehicleModelCollectionVehicleModelToAttach : vehicleMake.getVehicleModelCollection()) {
                vehicleModelCollectionVehicleModelToAttach = em.getReference(vehicleModelCollectionVehicleModelToAttach.getClass(), vehicleModelCollectionVehicleModelToAttach.getModel());
                attachedVehicleModelCollection.add(vehicleModelCollectionVehicleModelToAttach);
            }
            vehicleMake.setVehicleModelCollection(attachedVehicleModelCollection);
            em.persist(vehicleMake);
            for (VehicleOfInterest vehicleOfInterestCollectionVehicleOfInterest : vehicleMake.getVehicleOfInterestCollection()) {
                VehicleMake oldMakeOfVehicleOfInterestCollectionVehicleOfInterest = vehicleOfInterestCollectionVehicleOfInterest.getMake();
                vehicleOfInterestCollectionVehicleOfInterest.setMake(vehicleMake);
                vehicleOfInterestCollectionVehicleOfInterest = em.merge(vehicleOfInterestCollectionVehicleOfInterest);
                if (oldMakeOfVehicleOfInterestCollectionVehicleOfInterest != null) {
                    oldMakeOfVehicleOfInterestCollectionVehicleOfInterest.getVehicleOfInterestCollection().remove(vehicleOfInterestCollectionVehicleOfInterest);
                    oldMakeOfVehicleOfInterestCollectionVehicleOfInterest = em.merge(oldMakeOfVehicleOfInterestCollectionVehicleOfInterest);
                }
            }
            for (VehicleModel vehicleModelCollectionVehicleModel : vehicleMake.getVehicleModelCollection()) {
                VehicleMake oldMakeOfVehicleModelCollectionVehicleModel = vehicleModelCollectionVehicleModel.getMake();
                vehicleModelCollectionVehicleModel.setMake(vehicleMake);
                vehicleModelCollectionVehicleModel = em.merge(vehicleModelCollectionVehicleModel);
                if (oldMakeOfVehicleModelCollectionVehicleModel != null) {
                    oldMakeOfVehicleModelCollectionVehicleModel.getVehicleModelCollection().remove(vehicleModelCollectionVehicleModel);
                    oldMakeOfVehicleModelCollectionVehicleModel = em.merge(oldMakeOfVehicleModelCollectionVehicleModel);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findVehicleMake(vehicleMake.getMake()) != null) {
                throw new PreexistingEntityException("VehicleMake " + vehicleMake + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(VehicleMake vehicleMake) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            VehicleMake persistentVehicleMake = em.find(VehicleMake.class, vehicleMake.getMake());
            Collection<VehicleOfInterest> vehicleOfInterestCollectionOld = persistentVehicleMake.getVehicleOfInterestCollection();
            Collection<VehicleOfInterest> vehicleOfInterestCollectionNew = vehicleMake.getVehicleOfInterestCollection();
            Collection<VehicleModel> vehicleModelCollectionOld = persistentVehicleMake.getVehicleModelCollection();
            Collection<VehicleModel> vehicleModelCollectionNew = vehicleMake.getVehicleModelCollection();
            List<String> illegalOrphanMessages = null;
            for (VehicleOfInterest vehicleOfInterestCollectionOldVehicleOfInterest : vehicleOfInterestCollectionOld) {
                if (!vehicleOfInterestCollectionNew.contains(vehicleOfInterestCollectionOldVehicleOfInterest)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain VehicleOfInterest " + vehicleOfInterestCollectionOldVehicleOfInterest + " since its make field is not nullable.");
                }
            }
            for (VehicleModel vehicleModelCollectionOldVehicleModel : vehicleModelCollectionOld) {
                if (!vehicleModelCollectionNew.contains(vehicleModelCollectionOldVehicleModel)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain VehicleModel " + vehicleModelCollectionOldVehicleModel + " since its make field is not nullable.");
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
            vehicleMake.setVehicleOfInterestCollection(vehicleOfInterestCollectionNew);
            Collection<VehicleModel> attachedVehicleModelCollectionNew = new ArrayList<VehicleModel>();
            for (VehicleModel vehicleModelCollectionNewVehicleModelToAttach : vehicleModelCollectionNew) {
                vehicleModelCollectionNewVehicleModelToAttach = em.getReference(vehicleModelCollectionNewVehicleModelToAttach.getClass(), vehicleModelCollectionNewVehicleModelToAttach.getModel());
                attachedVehicleModelCollectionNew.add(vehicleModelCollectionNewVehicleModelToAttach);
            }
            vehicleModelCollectionNew = attachedVehicleModelCollectionNew;
            vehicleMake.setVehicleModelCollection(vehicleModelCollectionNew);
            vehicleMake = em.merge(vehicleMake);
            for (VehicleOfInterest vehicleOfInterestCollectionNewVehicleOfInterest : vehicleOfInterestCollectionNew) {
                if (!vehicleOfInterestCollectionOld.contains(vehicleOfInterestCollectionNewVehicleOfInterest)) {
                    VehicleMake oldMakeOfVehicleOfInterestCollectionNewVehicleOfInterest = vehicleOfInterestCollectionNewVehicleOfInterest.getMake();
                    vehicleOfInterestCollectionNewVehicleOfInterest.setMake(vehicleMake);
                    vehicleOfInterestCollectionNewVehicleOfInterest = em.merge(vehicleOfInterestCollectionNewVehicleOfInterest);
                    if (oldMakeOfVehicleOfInterestCollectionNewVehicleOfInterest != null && !oldMakeOfVehicleOfInterestCollectionNewVehicleOfInterest.equals(vehicleMake)) {
                        oldMakeOfVehicleOfInterestCollectionNewVehicleOfInterest.getVehicleOfInterestCollection().remove(vehicleOfInterestCollectionNewVehicleOfInterest);
                        oldMakeOfVehicleOfInterestCollectionNewVehicleOfInterest = em.merge(oldMakeOfVehicleOfInterestCollectionNewVehicleOfInterest);
                    }
                }
            }
            for (VehicleModel vehicleModelCollectionNewVehicleModel : vehicleModelCollectionNew) {
                if (!vehicleModelCollectionOld.contains(vehicleModelCollectionNewVehicleModel)) {
                    VehicleMake oldMakeOfVehicleModelCollectionNewVehicleModel = vehicleModelCollectionNewVehicleModel.getMake();
                    vehicleModelCollectionNewVehicleModel.setMake(vehicleMake);
                    vehicleModelCollectionNewVehicleModel = em.merge(vehicleModelCollectionNewVehicleModel);
                    if (oldMakeOfVehicleModelCollectionNewVehicleModel != null && !oldMakeOfVehicleModelCollectionNewVehicleModel.equals(vehicleMake)) {
                        oldMakeOfVehicleModelCollectionNewVehicleModel.getVehicleModelCollection().remove(vehicleModelCollectionNewVehicleModel);
                        oldMakeOfVehicleModelCollectionNewVehicleModel = em.merge(oldMakeOfVehicleModelCollectionNewVehicleModel);
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
                String id = vehicleMake.getMake();
                if (findVehicleMake(id) == null) {
                    throw new NonexistentEntityException("The vehicleMake with id " + id + " no longer exists.");
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
            VehicleMake vehicleMake;
            try {
                vehicleMake = em.getReference(VehicleMake.class, id);
                vehicleMake.getMake();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The vehicleMake with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<VehicleOfInterest> vehicleOfInterestCollectionOrphanCheck = vehicleMake.getVehicleOfInterestCollection();
            for (VehicleOfInterest vehicleOfInterestCollectionOrphanCheckVehicleOfInterest : vehicleOfInterestCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This VehicleMake (" + vehicleMake + ") cannot be destroyed since the VehicleOfInterest " + vehicleOfInterestCollectionOrphanCheckVehicleOfInterest + " in its vehicleOfInterestCollection field has a non-nullable make field.");
            }
            Collection<VehicleModel> vehicleModelCollectionOrphanCheck = vehicleMake.getVehicleModelCollection();
            for (VehicleModel vehicleModelCollectionOrphanCheckVehicleModel : vehicleModelCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This VehicleMake (" + vehicleMake + ") cannot be destroyed since the VehicleModel " + vehicleModelCollectionOrphanCheckVehicleModel + " in its vehicleModelCollection field has a non-nullable make field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(vehicleMake);
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

    public List<VehicleMake> findVehicleMakeEntities() {
        return findVehicleMakeEntities(true, -1, -1);
    }

    public List<VehicleMake> findVehicleMakeEntities(int maxResults, int firstResult) {
        return findVehicleMakeEntities(false, maxResults, firstResult);
    }

    private List<VehicleMake> findVehicleMakeEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(VehicleMake.class));
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

    public VehicleMake findVehicleMake(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(VehicleMake.class, id);
        } finally {
            em.close();
        }
    }

    public int getVehicleMakeCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<VehicleMake> rt = cq.from(VehicleMake.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
