///[Error:+|15]

//suma entre tipo int y boolean
class B {
    boolean x;
    int z;
}

class A extends B {

    int y;

    void met() {
        var x = this.y + 3 + this.z;
        var i = y().x + 3;
    }

    B y() {
    }
}