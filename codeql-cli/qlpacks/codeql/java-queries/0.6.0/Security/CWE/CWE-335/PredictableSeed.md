# Use of a predictable seed in a secure random number generator
Using a predictable seed in a pseudo-random number generator can lead to predictability of the numbers generated by it.


## Recommendation
If the predictability of the pseudo-random number generator does not matter then consider using the faster `Random` class from `java.util`. If it is important that the pseudo-random number generator produces completely unpredictable values then either let the generator securely seed itself by not specifying a seed or specify a randomly generated, unpredictable seed.


## Example
In the first example shown here, a constant value is used as a seed. Depending on the implementation of ` SecureRandom`, this could lead to the same random number being generated each time the code is executed.

In the second example shown here, the system time is used as a seed. Depending on the implementation of ` SecureRandom`, if an attacker knows what time the code was run, they could predict the generated random number.

In the third example shown here, the random number generator is allowed to generate its own seed, which it will do in a secure way.


```java
SecureRandom prng = new SecureRandom();
int randomData = 0;

// BAD: Using a constant value as a seed for a random number generator means all numbers it generates are predictable.
prng.setSeed(12345L);
randomData = prng.next(32);

// BAD: System.currentTimeMillis() returns the system time which is predictable.
prng.setSeed(System.currentTimeMillis());
randomData = prng.next(32);

// GOOD: SecureRandom implementations seed themselves securely by default.
prng = new SecureRandom();
randomData = prng.next(32);

```

## References
* Common Weakness Enumeration: [CWE-335](https://cwe.mitre.org/data/definitions/335.html).
* Common Weakness Enumeration: [CWE-337](https://cwe.mitre.org/data/definitions/337.html).
