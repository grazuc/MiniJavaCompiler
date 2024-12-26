///[Error:C|5]

//test herencia circular entre clases

class A extends C {

}

class B extends A {
    static void main(){}
}

class C extends B {

}