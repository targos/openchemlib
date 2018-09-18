/*
* Copyright (c) 1997 - 2016
* Actelion Pharmaceuticals Ltd.
* Gewerbestrasse 16
* CH-4123 Allschwil, Switzerland
*
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*
* 1. Redistributions of source code must retain the above copyright notice, this
*    list of conditions and the following disclaimer.
* 2. Redistributions in binary form must reproduce the above copyright notice,
*    this list of conditions and the following disclaimer in the documentation
*    and/or other materials provided with the distribution.
* 3. Neither the name of the the copyright holder nor the
*    names of its contributors may be used to endorse or promote products
*    derived from this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
* ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
* WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
* ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
* (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
* LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
* ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
* (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
* SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*
*/

package com.actelion.research.chem.reaction;

import java.util.ArrayList;
import java.util.Arrays;

import com.actelion.research.chem.DrawingObjectList;
import com.actelion.research.chem.Molecule;
import com.actelion.research.chem.StereoMolecule;

public class Reaction implements java.io.Serializable {
	static final long serialVersionUID = 0x2006CAFE;

	private ArrayList<StereoMolecule> mReactant;
	private ArrayList<StereoMolecule> mProduct;
	private ArrayList<StereoMolecule> mCatalyst;
	private DrawingObjectList mDrawingObjectList;
	private String mName;
	private boolean mReactionLayoutRequired;

	public Reaction() {
		mReactant = new ArrayList<StereoMolecule>();
		mProduct = new ArrayList<StereoMolecule>();
		mCatalyst = new ArrayList<StereoMolecule>();
		}

	public Reaction(String name) {
		this();
		mName = name;
		}

	public boolean isEmpty() {
		for (StereoMolecule mol:mReactant)
			if (mol.getAllAtoms() != 0)
				return false;

		for (StereoMolecule mol:mProduct)
			if (mol.getAllAtoms() != 0)
				return false;

		for (StereoMolecule mol:mCatalyst)
			if (mol.getAllAtoms() != 0)
				return false;

		return true;
		}

	public Reaction(Reaction rxn) {
		this();
		int r = (rxn == null) ? 0 : (rxn.mReactant == null ? 0 : rxn.mReactant.size());
		int p = (rxn == null) ? 0 : (rxn.mProduct == null ? 0 : rxn.mProduct.size());
		for (int i = 0; i < r; i++)
			mReactant.add(new StereoMolecule(rxn.getReactant(i)));
		for (int i = 0; i < p; i++)
			mProduct.add(new StereoMolecule(rxn.getProduct(i)));
		mDrawingObjectList = new DrawingObjectList(rxn.getDrawingObjects());
		}

	public Reaction(StereoMolecule[] mol, int reactantCount) {
		this();
		if (mol != null) {
			for (int i = 0; i < reactantCount; i++)
				mReactant.add(mol[i]);
			for (int i = reactantCount; i < mol.length; i++)
				mProduct.add(mol[i]);
			}
		}

	public StereoMolecule getReactant(int no) {
		return mReactant.get(no);
		}

	public int getReactants() {
		return mReactant.size();
		}

	public StereoMolecule getProduct(int no) {
		return mProduct.get(no);
		}

	public int getProducts() {
		return mProduct.size();
		}

	public StereoMolecule getCatalyst(int no) {
		return mCatalyst.get(no);
	}

	public int getCatalysts() {
		return mCatalyst.size();
	}

	/**
	 * @return count of reactants and products
	 */
	public int getMolecules() {
		return mReactant.size() + mProduct.size();
		}

	public StereoMolecule getMolecule(int no) {
		return (no < mReactant.size()) ?
			mReactant.get(no)
			: mProduct.get(no - mReactant.size());
		}

	public void addReactant(StereoMolecule reactant) {
		mReactant.add(reactant);
		}

	public void addReactant(StereoMolecule reactant, int position) {
		mReactant.add(position, reactant);
		}

	public void addProduct(StereoMolecule product) {
		mProduct.add(product);
		}

	public void addProduct(StereoMolecule product, int position) {
		mProduct.add(position, product);
		}

	public void addCatalyst(StereoMolecule catalyst) {
		mCatalyst.add(catalyst);
	}

	public void addCatalyst(StereoMolecule catalyst, int position) {
		mCatalyst.add(position, catalyst);
	}

	public String getName() {
		return (mName == null) ? "Unknown Reaction" : mName;
		}

	public void setName(String name) {
		mName = name;
		}

	public DrawingObjectList getDrawingObjects() {
		return mDrawingObjectList;
		}

	public void setDrawingObjects(DrawingObjectList l) {
		mDrawingObjectList = l;
		}

	public boolean isReactionLayoutRequired() {
		return mReactionLayoutRequired;
		}

	public void setReactionLayoutRequired(boolean b) {
		mReactionLayoutRequired = b;
		}

	/**
	 * Removes mapping numbers that are only used on one side of the reaction.
	 * Throws an exception if duplicate mapping numbers occur in reactants or products.
	 * @throws Exception
	 */
	public void validateMapping() throws Exception {
		StereoMolecule reactant, product;

		for (int i = 0; i < mReactant.size(); i++) {
			reactant = mReactant.get(i);
			for (int j = 0; j < reactant.getAllAtoms(); j++) {
				int mapNo = reactant.getAtomMapNo(j);
				if (mapNo != 0) {
					int found = 0;
					for (int k = 0; k < mProduct.size(); k++) {
						product = mProduct.get(k);
						for (int l = 0; l < product.getAllAtoms(); l++)
							if (product.getAtomMapNo(l) == mapNo)
								found++;
						}

					if (found == 0)
						reactant.setAtomMapNo(j, 0, false);
					else if (found > 1)
						throw new Exception("Duplicate mapping no in products");
					}
				}
			}

		for (int i = 0; i < mProduct.size(); i++) {
			product = mProduct.get(i);
			for (int j = 0; j < product.getAllAtoms(); j++) {
				int mapNo = product.getAtomMapNo(j);
				if (mapNo != 0) {
					int found = 0;
					for (int k = 0; k < mReactant.size(); k++) {
						reactant = mReactant.get(k);
						for (int l = 0; l < reactant.getAllAtoms(); l++)
							if (reactant.getAtomMapNo(l) == mapNo)
								found++;
						}

					if (found == 0)
						product.setAtomMapNo(j, 0, false);
					else if (found > 1)
						throw new Exception("Duplicate mapping no in reactants");
					}
				}
			}
		}

	/**
	 * This method determines the largest mapping number in use (maxMapNo), creates a boolean array[maxMapNo+1],
	 * and within this array flags every mapping number that refers to atoms, which change bonds in the course
	 * of the reaction. Mapped atoms that are connected to unpammed atoms are also considered being part of the
	 * reaction center. If the reaction is unmapped or has no reactants or products, then null is returned.
	 * @return null or boolean array indicating, which mapping numbers are referring to the reaction center
	 */
	public boolean[] getReactionCenterMapNos() {
		if (getReactants() == 0 || getProducts() == 0)
			return null;

		int maxMapNo = 0;
		for (int i=0; i<getMolecules(); i++) {
			StereoMolecule mol = getMolecule(i);
			mol.ensureHelperArrays(Molecule.cHelperParities);
			for (int atom=0; atom<mol.getAllAtoms(); atom++) {
				if (maxMapNo < mol.getAtomMapNo(atom))
					maxMapNo = mol.getAtomMapNo(atom);
				}
			}
		if (maxMapNo == 0)
			return null;

		// build mappings from mapNo to atom index in all products
		int[][] mapNo2Atom = new int[getProducts()][];
		for (int i=0; i<getProducts(); i++) {
			StereoMolecule product = getProduct(i);
			mapNo2Atom[i] = new int[maxMapNo+1];
			Arrays.fill(mapNo2Atom[i], -1);
			for (int atom=0; atom<product.getAllAtoms(); atom++) {
				int mapNo = product.getAtomMapNo(atom);
				if (mapNo != 0 && mapNo2Atom[i][mapNo] != -1)
					return null;	// same mapNo used twice in same product
				mapNo2Atom[i][mapNo] = atom;
			}
		}

		// find reaction centers as those mapped atoms that change bonding or are connected to unmapped atoms
		boolean[] isReactionCenter = new boolean[maxMapNo+1];
		for (int i=0; i<getReactants(); i++) {
			StereoMolecule reactant = getReactant(i);
			for (int rAtom=0; rAtom<reactant.getAllAtoms(); rAtom++) {
				int mapNo = reactant.getAtomMapNo(rAtom);
				if (mapNo != 0 && !isReactionCenter[mapNo]) {
					for (int j=0; j<getProducts(); j++) {
						int pAtom = mapNo2Atom[j][mapNo];
						if (pAtom != -1) {
							StereoMolecule product = getProduct(j);
							if (reactant.getConnAtoms(rAtom) != product.getConnAtoms(pAtom)) {
								isReactionCenter[mapNo] = true;
								break;
								}
							if (reactant.getAtomParity(rAtom) != product.getAtomParity(pAtom)) {	// TODO match neighbor positions for proper parity check
								isReactionCenter[mapNo] = true;
								break;
								}
							for (int k=0; k<reactant.getConnAtoms(rAtom); k++) {
								int connMapNo = reactant.getAtomMapNo(reactant.getConnAtom(rAtom, k));
								if (connMapNo == 0) {
									isReactionCenter[mapNo] = true;
									}
								else {
									int rBond = reactant.getConnBond(rAtom, k);
									boolean connMapNoFound = false;
									for (int l=0; l<product.getConnAtoms(pAtom); l++) {
										int productConnMapNo = product.getAtomMapNo(product.getConnAtom(pAtom, l));
										if (productConnMapNo == 0) {
											isReactionCenter[mapNo] = true;
											break;
											}
										if (productConnMapNo == connMapNo) {
											connMapNoFound = true;
											int pBond = product.getConnBond(pAtom, l);
											if ((reactant.isDelocalizedBond(rBond) ^ product.isDelocalizedBond(pBond))
													|| (!reactant.isDelocalizedBond(rBond)
													&& (reactant.getBondOrder(rBond) != product.getBondOrder(pBond)
													|| reactant.getBondParity(rBond) != product.getBondParity(pBond)))) {	// TODO match neighbor positions for proper parity check
												isReactionCenter[mapNo] = true;
												isReactionCenter[connMapNo] = true;
												break;
												}
											break;
											}
										}
									if (!connMapNoFound) {
										isReactionCenter[mapNo] = true;
										}
									}
								}
							}
						}
					}
				}
			}
		return isReactionCenter;
		}

	/**
	 * Fills an array mapping on the atoms for the given molecule of this reaction. Array bits are set if the respective atom
	 * has a mapping number flagged to be part of the reaction center, or if the atom has no mapping number but is connected
	 * to a mapped atom. The isReactionCenterAtom must be initialized with false before calling this method.
	 * @param moleculeNo reaction molecule index
	 * @param isReactionCenterMapNo flagged list of all reaction center mapping numbers, typically from getReactionCenterMapNos()
	 * @param isReactionCenterAtom null or empty array not smaller than the addressed molecule's total atom count
	 * @param reactionCenterAtom null or array not smaller than the addressed molecule's total atom count
	 * @return number of discovered reaction center atoms
	 */
	public int getReactionCenterAtoms(int moleculeNo, boolean[] isReactionCenterMapNo, boolean[] isReactionCenterAtom, int[] reactionCenterAtom) {
		StereoMolecule mol = getMolecule(moleculeNo);

		if (isReactionCenterAtom == null)
			isReactionCenterAtom = new boolean[mol.getAllAtoms()];

		int atomCount = 0;

		// mark atoms with known mapping numbers to be reaction centers
		for (int atom=0; atom<mol.getAllAtoms(); atom++) {
			if (isReactionCenterMapNo[mol.getAtomMapNo(atom)]) {
				isReactionCenterAtom[atom] = true;
				if (reactionCenterAtom != null)
					reactionCenterAtom[atomCount] = atom;
				atomCount++;
				}
			}

		// mapped atoms that connect to non mapped atoms are aleady covered, the non mapped ones, however, not
		for (int bond=0; bond<mol.getAllBonds(); bond++) {
			int atom1 = mol.getBondAtom(0, bond);
			int atom2 = mol.getBondAtom(1, bond);
			if (mol.getAtomMapNo(atom1) == 0 ^ mol.getAtomMapNo(atom2) == 0) {
				if (!isReactionCenterAtom[atom1]) {
					isReactionCenterAtom[atom1] = true;
					if (reactionCenterAtom != null)
						reactionCenterAtom[atomCount] = atom1;
					atomCount++;
					}
				if (!isReactionCenterAtom[atom2]) {
					isReactionCenterAtom[atom2] = true;
					if (reactionCenterAtom != null)
						reactionCenterAtom[atomCount] = atom2;
					atomCount++;
					}
				}
			}

		return atomCount;
		}

/*	public void removeEmptyMolecules() {
		int size = mReactant.size();
		for (int i = size-1; i >= 0; i--) {
			StereoMolecule mol = mReactant.get(i);
			if (mol.getAllAtoms() == 0) {
				mReactant.remove(i);
				}
			}
		size = mProduct.size();
		for (int i = size-1; i >= 0; i--) {
			StereoMolecule mol = mProduct.get(i);
			if (mol.getAllAtoms() == 0) {
				mProduct.remove(i);
				}
			}
		}*/
	}
