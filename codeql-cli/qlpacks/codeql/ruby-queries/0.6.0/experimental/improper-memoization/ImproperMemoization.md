# Improper Memoization
A common pattern in Ruby is to memoize the result of a method by storing it in an instance variable. If the method has no parameters, it can simply be stored directly in a variable.

```ruby

      def answer
        @answer ||= calculate_answer
      end
    
```
If the method takes parameters, then there are multiple results to store (one for each combination of parameter value). In this case the values should be stored in a hash, keyed by the parameter values.

```ruby

      def answer(x, y)
        @answer ||= {}
        @answer[x] ||= {}
        @answer[x][y] ||= calculate_answer
      end
    
```
If a memoization method takes parameters but does not include them in the memoization key, subsequent calls to the method with different parameter values may incorrectly return the same result. This can lead to the method returning stale data, or leaking sensitive information.


## Example
In this example, the method does not include its parameters in the memoization key. The first call to this method will cache the result, and subsequent calls will return the same result regardless of what arguments are given.

```ruby

      def answer(x, y)
        @answer ||= calculate_answer(x, y)
      end
    
```
This can be fixed by storing the result of `calculate_answer` in a hash, keyed by the parameter values.

```ruby

      def answer(x, y)
        @answer ||= {}
        @answer[x] ||= {}
        @answer[x][y] ||= calculate_answer(x, y)
      end
    
```
Note that if the result of `calculate_answer` is `false` or `nil`, then it will not be cached. To cache these values you can use a different pattern:

```ruby

      def answer(x, y)
        @answer ||= Hash.new do |h1, x|
          h1[x] = Hash.new do |h2, y|
            h2[y] = calculate_answer(x, y)
          end
        end
        @answer[x][y]
      end
    
```
