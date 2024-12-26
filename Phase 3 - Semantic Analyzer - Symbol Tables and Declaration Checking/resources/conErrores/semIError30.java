///[Error:B|5]

//test herencia circular

class A extends B {

}

class B extends A {
    static void main(){}
}