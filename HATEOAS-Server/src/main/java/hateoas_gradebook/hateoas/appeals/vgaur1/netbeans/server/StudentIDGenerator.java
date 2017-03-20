/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hateoas_gradebook.hateoas.appeals.vgaur1.netbeans.server;

import java.util.Random;
/**
 *
 * @author Varun_Gaur
 */

public class StudentIDGenerator {
    Random rand = new Random();
    private final String identifier;

    public StudentIDGenerator(String identifier) {
        this.identifier = identifier;
    }
    
    public StudentIDGenerator() {
        //int randomNum = rand.nextInt((9999999 - 70000) + 1) + 70000;
        this(java.util.UUID.randomUUID().toString());
        //this(Integer.toString(randomNum));
    }

    public String toString() {
        return identifier;
    }
}