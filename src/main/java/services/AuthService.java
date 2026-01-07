package services;

import entities.Customer;

import java.util.HashMap;
import java.util.Map;
import exceptions.AuthenticationFailedException;
import exceptions.DuplicateAccountException;

public class AuthService {
    //<username, User(Customer + Password)>
    private HashMap<String, Customer> customers;

    //
    public Customer login(String username, String password) throws AuthenticationFailedException {
        Customer result = null;
        if (customers.containsKey(username)) {
            Customer customer = customers.get(username);
            if (customer.getPassword().equals(password.hashCode())) {
                result = customer;
            }
        }
        if (result == null) {
            throw new AuthenticationFailedException();
        }
        return result;
    }

    public Customer register(String username, String firstName, String lastName, String email, String password) throws DuplicateAccountException {
        if (customers.containsKey(username)) {
            throw new DuplicateAccountException();
        }
        Customer customer = new Customer(firstName, lastName, email, Integer.toString(password.hashCode()));
        customers.put(username, customer);
        return customer;
    }

    public Map<String, Customer> getCustomers() {
        return new HashMap<>(customers);
    }

}
