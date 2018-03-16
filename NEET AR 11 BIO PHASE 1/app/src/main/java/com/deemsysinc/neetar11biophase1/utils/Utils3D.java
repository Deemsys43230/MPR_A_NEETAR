package com.deemsysinc.neetar11biophase1.utils;

import javax.vecmath.Vector3f;

/**
 * Created by Deemsys on 30-Jan-18.
 */

public class Utils3D {
    Vector3f val=new Vector3f();


    private BiquadFilterInstance[] inst = new BiquadFilterInstance[3];

    public Utils3D(double fc)
    {
        for(int i=0;i<3;i++){
            inst[i] = new BiquadFilterInstance(fc);
        }
    }
    public Vector3f update(Vector3f in){
        val.x = (float) inst[0].process(in.x);
        val.y = (float) inst[1].process(in.y);
        val.z = (float) inst[2].process(in.z);
        return val;
    }

    private class BiquadFilterInstance {
        double a0, a1, a2, b1, b2;
        double Fc=0.5,Q=0.707, peakGain=0.0;
        double z1=0.0, z2=0.0;

        BiquadFilterInstance(double fc){
            Fc = fc;
            calcBiquad();
        }

        double process(double in){
            double out = in * a0 + z1;
            z1 = in * a1 + z2 - b1 * out;
            z2 = in * a2 - b2 * out;
            return out;
        }

        void calcBiquad() {
            double norm;
            double K = Math.tan(Math.PI * Fc);
            norm = 1 / (1 + K / Q + K * K);
            a0 = K * K * norm;
            a1 = 2 * a0;
            a2 = a0;
            b1 = 2 * (K * K - 1) * norm;
            b2 = (1 - K / Q + K * K) * norm;
        }
    }
    public static boolean distanceCheck(Vector3f newPoint, Vector3f lastPoint) {
        Vector3f temp = new Vector3f();
        temp.sub(newPoint, lastPoint);
        if (temp.length() > AppSettings.getMinDistance()) {
            return true;
        }
        return false;
    }


}
