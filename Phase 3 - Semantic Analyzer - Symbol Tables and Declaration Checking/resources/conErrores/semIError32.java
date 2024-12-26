///[Error:B|9]

//test herencia circular entre interfaces

class Init{
    static void main(){}
}

interface A extends B {

}

interface B extends A {

}