package hr.algebra.movie.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import hr.algebra.movie.R
import hr.algebra.movie.adapter.ItemAdapter
import hr.algebra.movie.databinding.FragmentItemsBinding
import hr.algebra.movie.framework.fetchItems
import hr.algebra.movie.model.Item


class ItemsFragment : Fragment() {

    private lateinit var items: MutableList<Item>
    private lateinit var binding: FragmentItemsBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        items = requireContext().fetchItems()
        binding = FragmentItemsBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvItems.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = ItemAdapter(requireContext(), items)
        }
    }
}