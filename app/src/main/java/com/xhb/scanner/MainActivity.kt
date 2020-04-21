package com.xhb.scanner

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.xhb.scanner.module.Person
import spica.exception.SpicaException

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        test()
        testPerson()
    }

    private fun testPerson(): Person {
        val person = Person()
        person.age = 100
        person.school = "nanTong"
        return person
    }

    private fun test() {
//        val person = Person()
//        person.age = 10
//        person.school = "NanTong"

        System.out.println("hello world")
        throw SpicaException("man do")
    }


}
