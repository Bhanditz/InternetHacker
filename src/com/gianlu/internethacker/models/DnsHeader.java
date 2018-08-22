package com.gianlu.internethacker.models;

import com.gianlu.internethacker.Utils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class DnsHeader {
    public final short id;
    public final boolean qr;
    public final OpCode opcode;
    public final boolean aa;
    public final boolean tc;
    public final boolean rd;
    public final boolean ra;
    public final RCode rcode;
    public final short qdcount;
    public final short ancount;
    public final short nscount;
    public final short arcount;

    DnsHeader(ByteBuffer data) {
        id = data.getShort();

        byte byte2 = data.get();
        qr = ((byte2 >> 7) & 0b00000001) != 0;
        opcode = OpCode.parse((byte2 >> 3) & 0b00001111);
        aa = ((byte2 >> 2) & 0b00000001) != 0;
        tc = ((byte2 >> 1) & 0b00000001) != 0;
        rd = ((byte2  /* >> 0 */) & 0b00000001) != 0;

        byte byte3 = data.get();
        ra = ((byte3 >> 7) & 0b00000001) != 0;
        int z = (byte3 >> 4) & 0b00000111;
        if (z != 0) throw new RuntimeException("Z should be 0, something went wrong!");
        rcode = RCode.parse((byte3 /* >> 0 */) & 0b00001111);

        qdcount = data.getShort();
        ancount = data.getShort();
        nscount = data.getShort();
        arcount = data.getShort();
    }

    DnsHeader(DnsHeader header) {
        this.id = header.id;
        this.qr = header.qr;
        this.opcode = header.opcode;
        this.aa = header.aa;
        this.tc = header.tc;
        this.rd = header.rd;
        this.ra = header.ra;
        this.rcode = header.rcode;
        this.qdcount = header.qdcount;
        this.ancount = header.ancount;
        this.nscount = header.nscount;
        this.arcount = header.arcount;
    }

    private DnsHeader(short id, boolean qr, OpCode opcode, boolean aa, boolean tc, boolean rd, boolean ra, RCode rcode, short qdcount, short ancount, short nscount, short arcount) {
        this.id = id;
        this.qr = qr;
        this.opcode = opcode;
        this.aa = aa;
        this.tc = tc;
        this.rd = rd;
        this.ra = ra;
        this.rcode = rcode;
        this.qdcount = qdcount;
        this.ancount = ancount;
        this.nscount = nscount;
        this.arcount = arcount;
    }

    public void write(OutputStream out) throws IOException {
        Utils.putShort(out, id);

        byte b = 0;
        b |= (qr ? 1 : 0) << 7;
        b |= opcode.val << 3;
        b |= (aa ? 1 : 0) << 2;
        b |= (tc ? 1 : 0) << 1;
        b |= (rd ? 1 : 0) /* << 0 */;
        out.write(b);

        b = 0;
        b |= (ra ? 1 : 0) << 7;
        b |= 0 /* << 4 */; /* z */
        b |= rcode.val /* << 0 */;
        out.write(b);

        Utils.putShort(out, qdcount);
        Utils.putShort(out, ancount);
        Utils.putShort(out, nscount);
        Utils.putShort(out, arcount);
    }

    public Builder buildUpon() {
        return new Builder(this);
    }

    public enum RCode {
        NO_ERROR(0),
        FORMAT_ERROR(1),
        SERVER_FAILURE(2),
        NAME_ERROR(3),
        NOT_IMPLEMENTED(4),
        REFUSED(5);

        private final int val;

        RCode(int val) {
            this.val = val;
        }

        @NotNull
        public static RCode parse(int val) {
            for (RCode code : values())
                if (code.val == val)
                    return code;

            throw new IllegalArgumentException("Unknown RCODE for " + val);
        }
    }

    public enum OpCode {
        QUERY(0),
        IQUERY(1),
        STATUS(2);

        private final int val;

        OpCode(int val) {
            this.val = val;
        }

        @NotNull
        public static OpCode parse(int val) {
            for (OpCode code : values())
                if (code.val == val)
                    return code;

            throw new IllegalArgumentException("Unknown OPCODE for " + val);
        }
    }

    public static class Builder {
        private short id;
        private boolean qr;
        private OpCode opcode;
        private boolean aa;
        private boolean tc;
        private boolean rd;
        private boolean ra;
        private RCode rcode;
        private short qdcount;
        private short ancount;
        private short nscount;
        private short arcount;

        private Builder(DnsHeader header) {
            id = header.id;
            qr = header.qr;
            opcode = header.opcode;
            aa = header.aa;
            tc = header.tc;
            rd = header.rd;
            ra = header.ra;
            rcode = header.rcode;
            qdcount = header.qdcount;
            ancount = header.ancount;
            nscount = header.nscount;
            arcount = header.arcount;
        }

        public Builder() {
        }

        public Builder setId(short id) {
            this.id = id;
            return this;
        }

        public Builder setQr(boolean qr) {
            this.qr = qr;
            return this;
        }

        public Builder setOpcode(OpCode opcode) {
            this.opcode = opcode;
            return this;
        }

        public Builder setAa(boolean aa) {
            this.aa = aa;
            return this;
        }

        public Builder setTc(boolean tc) {
            this.tc = tc;
            return this;
        }

        public Builder setRd(boolean rd) {
            this.rd = rd;
            return this;
        }

        public Builder setRa(boolean ra) {
            this.ra = ra;
            return this;
        }

        public Builder setRcode(RCode rcode) {
            this.rcode = rcode;
            return this;
        }

        public Builder setQdcount(short qdcount) {
            this.qdcount = qdcount;
            return this;
        }

        public Builder setAncount(short ancount) {
            this.ancount = ancount;
            return this;
        }

        public Builder setNscount(short nscount) {
            this.nscount = nscount;
            return this;
        }

        public Builder setArcount(short arcount) {
            this.arcount = arcount;
            return this;
        }

        public DnsHeader build() {
            return new DnsHeader(id, qr, opcode, aa, tc, rd, ra, rcode, qdcount, ancount, nscount, arcount);
        }
    }
}