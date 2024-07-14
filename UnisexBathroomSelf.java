public class UnisexBathroom{
    int MAX_CAPACITY;
    int enteredMales = 0;
    int enteredFemales = 0;
    List<Person> enteredPeople = new ArrrayList<>();
    ReentrantLock genderLock = new ReentrantLock();
    Condition oppositeGenderPresent = new Condition();
    Condition maxLimitReached = new Condition();
    UnisexBathroom(int maxCapacity){
        this.MAX_CAPACITY = maxCapacity;
    }
    private void useBathroom(Person p){
        System.out.println( p.name +" has started using bathroom." );
        Thread.sleep(1000);
        System.out.println(p.name + " is done using the bathroom.");

    }
    public void enter(Person p){
        if(p.gender == "male"){
            genderLock.lock();
            while(enteredFemale > 0){
                OppositeGenderPresent.await();
                genderLock.unlock(); //
                genderLock.lock(); //
            }
            while(enteredFemale + enteredMale >= MAX_CAPACITY){
                maxLimitReached.await();
            }
            enteredMale++;
            genderLock.unlock();
            useBathroom();
            genderLock.lock();
            enteredMale--;
            maxLimitReached.signalAll();
            if(enteredMale == 0){
                System.out.println("Bathroom is Empty");
                OppositeGenderPresent.signalAll();
            }
            genderLock.unlock();

        }else{
            genderLock.lock();
            while(enteredMale > 0){
                OppositeGenderPresent.await();
                genderLock.unlock();
                genderLock.lock();
            }
            while(enteredFemale + enteredMale >= MAX_CAPACITY){
                maxLimitReached.await();
            }
            enteredFemale++;
            genderLock.unlock();
            useBathroom(p);
            genderLock.lock();
            enteredFemale--;
            maxLimitReached.signalAll();
            if(enteredFemale == 0){
                System.out.println("Bathroom is Empty");
                OppositeGenderPresent.signalAll();
            }
            genderLock.unlock();

        }
    }

}