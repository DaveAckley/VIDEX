package com.putable.hyperspace.core;

import java.awt.Color;
import java.util.Random;

public enum BitValue {
	BIT_VARIABLE_UNKNOWN {
		@Override
		public Color getFill() {
			return Color.decode("#888888");
		}

		@Override
		public Color getBorder() {
			return Color.decode("#555555");
		}

		@Override
		public BitValue opposite() {
			return BIT_VARIABLE_UNKNOWN;
		}

		@Override
		public int asInt() {
			return 0;
		}
	},
	BIT_FIXED_UNKNOWN {
		@Override
		public Color getFill() {
			return Color.decode("#888888");
		}

		@Override
		public Color getBorder() {
			return Color.decode("#bbbbbb");
		}

		@Override
		public BitValue opposite() {
			return BIT_FIXED_UNKNOWN;
		}

		@Override
		public int asInt() {
			return 0;
		}
	},
	BIT_ON {
		@Override
		public Color getFill() {
			return Color.decode("#ffffff");
		}

		@Override
		public Color getBorder() {
			return BIT_VARIABLE_UNKNOWN.getBorder();
		}

		@Override
		public BitValue opposite() {
			return BIT_OFF;
		}

		@Override
		public int asInt() {
			return 1;
		}
	},
	BIT_OFF {
		@Override
		public Color getFill() {
			return Color.decode("#000000");
		}

		@Override
		public Color getBorder() {
			return BIT_VARIABLE_UNKNOWN.getBorder();
		}

		@Override
		public BitValue opposite() {
			return BIT_ON;
		}

		@Override
		public int asInt() {
			return -1;
		}

	},
	BIT_FIXED_ON {
		@Override
		public Color getFill() {
			return Color.decode("#bbbbbb");
		}

		@Override
		public Color getBorder() {
			return BIT_FIXED_UNKNOWN.getBorder();
		}

		@Override
		public BitValue opposite() {
			return BIT_FIXED_OFF;
		}

		@Override
		public int asInt() {
			return 1;
		}
	},
	BIT_FIXED_OFF {
		@Override
		public Color getFill() {
			return Color.decode("#666666");
		}

		@Override
		public Color getBorder() {
			return BIT_FIXED_UNKNOWN.getBorder();
		}

		@Override
		public BitValue opposite() {
			return BIT_FIXED_ON;
		}

		@Override
		public int asInt() {
			return -1;
		}
	},
	BIT_INVISIBLE{
		@Override
		public Color getFill() {
			return Color.black;
		}

		@Override
		public Color getBorder() {
			return BIT_INVISIBLE.getFill();
		}

		@Override
		public BitValue opposite() {
			return BIT_INVISIBLE;
		}

		@Override
		public int asInt() {
			return 0;
		}
	}
	;
	public abstract Color getFill() ;
	public abstract Color getBorder() ;
	public abstract BitValue opposite() ;
	public abstract int asInt() ;
	public static BitValue random(Random random, double onFrac, boolean isvar) {
		boolean isOn = random.nextDouble() < onFrac;
		if (isOn  && isvar) return BIT_ON;
		if (!isOn && isvar) return BIT_OFF;
		if (isOn  && !isvar) return BIT_FIXED_ON;
		if (!isOn && !isvar) return BIT_FIXED_OFF;
		throw new IllegalStateException(); // don't be a dork
	}
}
