///[Error:C|4]

//test herencia circular
class A extends C {
    static void main(){}
}

class B extends A {

}



class C extends A{}