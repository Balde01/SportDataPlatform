package com.sportdataauth.domain.value;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Objects;

import com.sportdataauth.domain.exception.EmailNotAllowedException;

public final class Email {

    private final String value;

    private Email(String canonical) {
        this.value = canonical;
    }

    public static Email of(String raw) {
        if (raw == null) {
            throw new EmailNotAllowedException();
        }

        String canonical = raw.trim();
        canonical = Normalizer.normalize(canonical, Normalizer.Form.NFKC);
        canonical = canonical.toLowerCase(Locale.ROOT);

        if (canonical.isBlank()) {
            throw new  EmailNotAllowedException();
        }

        return new Email(canonical);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email)) return false;
        Email email = (Email) o;
        return value.equals(email.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
