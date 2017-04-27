package com.minteronline.josh.jgraph.gui;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayDeque;
import java.util.ArrayList;

public class JGraphRuntime {
	private static final byte CMD_PUSH	= (byte)0x00;
	private static final byte CMD_PUSHX	= (byte)0x01;
	private static final byte CMD_XPUSH	= (byte)0x02;
	private static final byte CMD_XPOP	= (byte)0x03;
	
	private static final byte CMD_NEG	= (byte)0x40;
	private static final byte CMD_ABS	= (byte)0x41;
	
	private static final byte CMD_ADD	= (byte)0x80;
	private static final byte CMD_SUB	= (byte)0x81;
	private static final byte CMD_MOD	= (byte)0x82;
	private static final byte CMD_MUL	= (byte)0x83;
	private static final byte CMD_DIV	= (byte)0x84;
	private static final byte CMD_POW	= (byte)0x85;
	
	private static final byte CMD_SQRT	= (byte)0xC0;
	private static final byte CMD_LOG	= (byte)0xC1;
	private static final byte CMD_LN	= (byte)0xC2;
	private static final byte CMD_SIN	= (byte)0xC3;
	private static final byte CMD_COS	= (byte)0xC4;
	private static final byte CMD_TAN	= (byte)0xC5;
	private static final byte CMD_ASIN	= (byte)0xC6;
	private static final byte CMD_ACOS	= (byte)0xC7;
	private static final byte CMD_ATAN	= (byte)0xC8;
	
	private static final byte CMD_CALL	= (byte)0xFF;
	
	private static class Instruction {
		public Instruction(int c) {
			code = (byte)c;
		}
		public byte code;
		public double value;
	}
	
	private static final ArrayList<Instruction> CODE = new ArrayList<Instruction>();
	
	public static boolean load() {
		CODE.clear();
		return load("current.jgf");
	}
	
	private static boolean load(String name) {
		FileInputStream input = null;
		try {
			input = new FileInputStream(name);
			if (input.read() != 0x8B) {
				JGraphFrame.setResult("!Invalid Header");
				return false;
			}
			
			while (input.read() != 0);
			int code;
			while ((code = input.read()) != -1) {
				if ((byte)code == CMD_CALL) {
					CODE.add(new Instruction(CMD_XPUSH));
					
					StringBuilder builder = new StringBuilder();
					while ((code = input.read()) != 0) {
						builder.append(new char[] {(char)code});
					}
					if (new File("lib/" + builder.toString() + ".jgf").exists()) {
						if (!load("lib/" + builder.toString() + ".jgf")) {
							return false;
						}
					} else {
						if (!load("user/" + builder.toString() + ".jgf")) {
							return false;
						}
					}
					
					CODE.add(new Instruction(CMD_XPOP));
				} else {
					Instruction instruction = new Instruction(code);
					if (code == CMD_PUSH) {
						byte[] buffer = new byte[8];
						input.read(buffer);
						instruction.value = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).getDouble();
					}
					CODE.add(instruction);
				}
			}
			return true;
		} catch (Exception ex) {
			JGraphFrame.setResult("!" + ex.getMessage());
			return false;
		} finally {
			try {
				input.close();
			} catch (Exception ex) {}
		}
	}
	
	public static double eval(double x) {
		
		ArrayDeque<Double> stack = new ArrayDeque<Double>();
		ArrayDeque<Double> xstack = new ArrayDeque<Double>();
		xstack.addFirst(x);
		
		double right;
		for (Instruction instruction : CODE) {
			switch (instruction.code) {
			case CMD_PUSH:
				stack.addFirst(instruction.value);
				break;
			case CMD_PUSHX:
				stack.addFirst(xstack.peekFirst());
				break;
			case CMD_XPUSH:
				xstack.addFirst(stack.removeFirst());
				break;
			case CMD_XPOP:
				xstack.removeFirst();
				break;
			case CMD_NEG:
				stack.addFirst(-stack.removeFirst());
				break;
			case CMD_ABS:
				stack.addFirst(Math.abs(stack.removeFirst()));
				break;
			case CMD_ADD:
				right = stack.removeFirst();
				stack.addFirst(stack.removeFirst() + right);
				break;
			case CMD_SUB:
				right = stack.removeFirst();
				stack.addFirst(stack.removeFirst() - right);
				break;
			case CMD_MOD:
				right = stack.removeFirst();
				stack.addFirst(stack.removeFirst() % right);
				break;
			case CMD_MUL:
				right = stack.removeFirst();
				stack.addFirst(stack.removeFirst() * right);
				break;
			case CMD_DIV:
				right = stack.removeFirst();
				stack.addFirst(stack.removeFirst() / right);
				break;
			case CMD_POW:
				right = stack.removeFirst();
				stack.addFirst(Math.pow(stack.removeFirst(), right));
				break;
			case CMD_SQRT:
				stack.addFirst(Math.sqrt(stack.removeFirst()));
				break;
			case CMD_LOG:
				stack.addFirst(Math.log10(stack.removeFirst()));
				break;
			case CMD_LN:
				stack.addFirst(Math.log(stack.removeFirst()));
				break;
			case CMD_SIN:
				stack.addFirst(Math.sin(stack.removeFirst()));
				break;
			case CMD_COS:
				stack.addFirst(Math.cos(stack.removeFirst()));
				break;
			case CMD_TAN:
				stack.addFirst(Math.tan(stack.removeFirst()));
				break;
			case CMD_ASIN:
				stack.addFirst(Math.asin(stack.removeFirst()));
				break;
			case CMD_ACOS:
				stack.addFirst(Math.acos(stack.removeFirst()));
				break;
			case CMD_ATAN:
				stack.addFirst(Math.atan(stack.removeFirst()));
				break;
			}
		}
		
		return stack.removeFirst();
	}
}