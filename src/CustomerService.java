//initiera repo
public class CustomerService {
    private CustomerRepo customerRepo;



    public CustomerService() {

        customerRepo = new CustomerRepo();
    }


    public void registerCustomer(Customer customer) {
        customerRepo.registerCustomer(customer);
    }


    public void updateCustomerEmail(String name, String newEmail) {
        customerRepo.updateCustomerEmail(name, newEmail);
    }


    public boolean customerExists(String name) {
        return customerRepo.customerExists(name);
    }
}
