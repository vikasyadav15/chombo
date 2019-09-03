/*
 * chombo: Hadoop Map Reduce utility
 * Author: Pranab Ghosh
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0 
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */


package org.chombo.math;

import org.chombo.util.BasicUtils;
import org.chombo.util.Pair;

import Jama.Matrix;

/**
 * Math utilities
 * @author pranab
 *
 */
public class MathUtils {
	
	//student t distribution with infinite sample size
	public static final double[][] tDistr = new double[][] {
		{0.900, 1.282},
		{0.950, 1.645},
		{0.975, 1.960},
		{0.990, 2.326},
		{0.995, 2.576}
	};
	
	/**
	 * @param data
	 * @return
	 */
	public static double average(double[] data) {
		double sum = 0;
		for (double d : data){
			sum += d;
		}
		return sum / data.length;
	}
	
	/**
	 * @param data
	 * @param weight
	 * @return
	 */
	public static double weightedAverage(double[] data, double[] weight) {
		double sum = 0;
		double sumWt = 0;
		for (int i = 0; i < data.length; ++i){
			sum += data[i] * weight[i];
			sumWt += weight[i];
		}
		return sum / sumWt;
	}

	/**
	 * @param data
	 * @return
	 */
	public static double max(double[] data) {
		double max = Double.MIN_VALUE;
		for (double d : data){
			if (d > max) {
				max = d;
			}
		}
		return max;
	}

	/**
	 * @param data
	 * @return
	 */
	public static double min(double[] data) {
		double min = Double.MAX_VALUE;
		for (double d : data){
			if (d < min) {
				min = d;
			}
		}
		return min;
	}

	/**
     * @param cons
     * @param val
     * @return
     */
    public static double expScale(double cons, double val) {
    	double e = Math.exp(cons * val);
    	return (e - 1) / e;
    }
    
    /**
     * @param cons
     * @param val
     * @return
     */
    public static double logisticScale(double cons, double val) {
    	double e = Math.exp(-cons * val);
    	return 1 / (1 + e);
    }

    /**
     * @param table
     * @param x
     * @return
     */
    public static double linearInterpolate(double[][] table, double x) {
    	return linearInterpolate(table,  x, false);
    }   
    
    /**
     * @param table
     * @param x
     * @param withinRange
     * @return
     */
    public static double linearInterpolate(double[][] table, double x, boolean withinRange) {
    	double y = 0;
    	boolean found = false;
    	int numRows = table.length;
    	
    	for (int r = 0; r < numRows; ++r) {
    		if (r < numRows - 1) {
	    		double[] cRow = table[r];
	    		double[] nRow = table[r+1];
	    		if (x > cRow[0] && x <= nRow[0]) {
	    			y = cRow[1] + (nRow[1] - cRow[1]) / (nRow[0] - cRow[0]);
	    			found = true;
	    			break;
	    		}
    		}
    	}
    	
    	//outside range
    	if (!found) {
    		if (withinRange) {
    			throw new IllegalStateException("can not interplotate outside range");
    		} else {
    			if (x <= table[0][0]) {
    				//use smallest
    				y = table[0][1];
    			} else {
    				//use largest
    				y = table[numRows-1][1];
    			}
    		}
    	}
    	return y;
    }
    
    /**
     * @param table
     * @return
     */
    public static Pair<Double, Double> linearRegression(double[][] table) {
    	int count = table.length;
    	
    	double avX = 0;
    	double avY = 0;
    	for (int i = 0; i < count; ++i) {
    		avX += table[i][0];
    		avY += table[i][1];
    	}
    	avX /= count;
    	avY /= count;
    	
    	double s1 = 0;
    	double s2 = 0;
    	for (int i = 0; i < count; ++i) {
    		double diffX = table[i][0] - avX;
    		double diffY = table[i][1] - avY;
    		s1 += (diffX * diffY);
    		s2 += (diffX * diffX);
    	}
    	double b1 = s1 / s2;
    	double b0 = avY - b1 * avX;
    	Pair<Double, Double> coeff = new Pair<Double, Double>(b1, b0);
    	return coeff;
    }
    
    /**
     * @param data
     * @return
     */
    public static Matrix createMatrix(double[][] data) {
    	return new Matrix(data);
    }
    
    /**
     * @param data
     * @return
     */
    public static  double[][] invertMatrix(double[][] data) {
    	Matrix source = new Matrix(data);
    	double[][] inverted = source.inverse().getArray();
    	return inverted;
    }
    
    /**
     * @param data
     * @param numRows
     * @return
     */
    public static Matrix createColMatrix(double[] data, int numRows) {
    	Matrix m = null;
    	if (data.length == numRows) {
    		m = new Matrix(data, numRows);
    	} else {
    		throw new IllegalStateException("num of rows is not equal to array size");
    	}
    	return m;
    }
    
    /**
     * @param data
     * @return
     */
    public static Matrix createColMatrix(double[] data) {
    	int numRows = data.length;
        return new Matrix(data, numRows);
    }

    /**
     * @param data
     * @param numCols
     * @return
     */
    public static Matrix createRowMatrix(double[] data, int numCols) {
    	Matrix m = null;
    	if (data.length == numCols) {
    		m = new Matrix(data, 1);
    	} else {
    		throw new IllegalStateException("num of columns is not equal to array size");
    	}
    	return m;
    }
    
    /**
     * @param data
     * @param numCols
     * @return
     */
    public static Matrix createRowmatrix(double[] data) {
    	return  new Matrix(data, 1);
    }

    /**
     * @param a
     * @param b
     * @return
     */
    public static Matrix multiplyMatrix(Matrix a, Matrix b) {
    	return a.times(b);
    }

    /**
     * @param a
     * @param b
     * @return
     */
    public static double[][] multiplyMatrix(double[][] a, double[][] b) {
    	Matrix am = new Matrix(a);
    	Matrix bm = new Matrix(b);
    	Matrix c = am.times(bm);
    	return c.getArray();
    }
    
    /**
     * @param m
     * @return
     */
    public static double[] arrayFromColumnMatrix(Matrix m) {
    	double[][] data = m.getArray();
    	BasicUtils.assertCondition(data[0].length == 1, "not a column matrix");
    	int nCols = data.length;
    	double[] ar = new double[nCols];
    	for (int i = 0; i < nCols; ++i) {
    		ar[i] = data[i][0];
    	}
    	return ar;
    }

    public static double[] arrayFromRowMatrix(Matrix m) {
    	double[][] data = m.getArray();
    	BasicUtils.assertCondition(data.length == 1, "not a row matrix");
    	int nRows = data[0].length;
    	double[] ar = new double[nRows];
    	for (int j = 0; j < nRows; ++j) {
    		ar[j] = data[0][j];
    	}
    	return ar;
    }

    /**
     * @param a
     * @return
     */
    public static Matrix transposeMatrix(Matrix a) {
    	return a.transpose();
    }
    
    /**
     * @param a
     * @return
     */
    public static double[][] transposeMatrix(double[][] a) {
    	Matrix am = new Matrix(a);
    	Matrix c = am.transpose();
    	return c.getArray();
    }
    
    /**
     * @param number
     * @return
     */
    public static boolean isPowerOfTwo(int value) { 
    	//power of 2 is 1 followed by by bunch of 0 and  a number 1 less is 0 followed by bunch of 1
    	boolean isPower = false;
        if (value > 0) {
        	isPower = value == 1? true :  ((value & (value - 1)) == 0);
        }
        return isPower;
    }  
    
    /**
     * @param value
     * @return
     */
    public static int binaryPowerFloor(int value) {
    	int shifted = 1;
    	while(shifted < value) {
    		shifted <<= 1;
    	}
    	if (shifted > value){
    		shifted >>= 1;
    	}
    	return shifted;
    }

    /**
     * @param value
     * @return
     */
    public static int binaryPowerCeiling(int value) {
    	int shifted = 1;
    	while(shifted < value) {
    		shifted <<= 1;
    	}
    	return shifted;
    }
}
